/*
 *
 *  * Copyright 2012-2015 Viant.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy of
 *  * the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations under
 *  * the License.
 *
 */

package com.sm.connector.server;

import com.sm.connector.MRIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.CacheBlock;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.StoreException;
import voldemort.store.cachestore.Value;
import voldemort.store.cachestore.impl.SoftReferenceImpl;
import voldemort.store.cachestore.voldeimpl.StoreIterator;
import voldemort.utils.Pair;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static voldemort.store.cachestore.BlockUtil.*;

/**
 * Created by mhsieh on 8/6/14.
 */
public class ServerStore extends StoreIterator implements MRIterator {
    private static Log logger = LogFactory.getLog(ServerStore.class);
    // current offset of dataf
    protected volatile long dataOffset = 0;
    // key channel offset
    protected volatile long keyOffset = 0;
    protected Lock lock = new ReentrantLock();

    public ServerStore(String filename) {
        this(filename, false);
    }
    public ServerStore(String filename, boolean flag) {
        super(filename, flag);
        try {
            dataOffset = dataChannel.size();
            keyOffset = keyChannel.size();
        } catch (Exception ex) {
            throw new RuntimeException( ex.getMessage(), ex);
        }
    }

    /**
     *
     * @param begin of record no, it is zero based
     * @param size
     * @return
     */
    public List<Pair<byte[],byte[]>> nextBlock(int begin, int size)  {
        if ( begin > totalRecord - 1 ) return new ArrayList<Pair<byte[], byte[]>>();
        else {
            List<Pair<byte[],byte[]>> list = new ArrayList<Pair<byte[], byte[]>>(size);
            ByteBuffer buf = ByteBuffer.allocate(RECORD_SIZE);
            int current = begin ;
            while ( hasNext() ) {
                try {
                    Pair<byte[], byte[]> pair = next(current, buf);
                    if (pair != null) {
                        list.add(pair);
                        current++;
                        if (current >= begin + size)
                            break;
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
            logger.info("nextBlock begin "+begin+ " size "+list.size());
            return list;
        }
    }


    /**
     * curRecord is 0 base, so set it by current -1
     * if you use 1 base
      * @param current
     */
    public void setCurrent(int current) {
        if ( current >= getTotalRecord() )
            throw new RuntimeException("current "+current+ " >= total "+getTotalRecord() );
        else {
            curRecord = current;
        }
    }

    public boolean isEnd(int last) {
        if ( getCurRecord() < last  )
            return false;
        else
            return true;
    }

    public String getFilename() {
        return filename;
    }

    public boolean writeRecord(Key key, Value<byte[]> value,int recordNo) throws Exception {
        if ( recordNo > totalRecord ) {
            logger.warn("record# "+recordNo+" > "+totalRecord);
            return false;
        }
        else {
            byte[] ks = toKeyBytes( key);
            if ( recordNo < totalRecord ) {
                long pos = OFFSET + (long) curRecord * RECORD_SIZE;
                indexChannel.read(buf, pos);
                buf.rewind();
                byte status = buf.get();
                if ( isDeleted(status) ) {
                    logger.warn("update a delete record "+key.toString());
                    return false;
                }
                else {
                    long keyLen = buf.getLong();
                    byte[] keyBytes = readChannel( keyLen, keyChannel);
                    if (! ks.equals( keyBytes)) {
                        logger.info("record # "+recordNo+" not the same key "+key.toString()) ;
                        return false;
                    }
                    long data = buf.getLong();
                    long block2version = buf.getLong();
                    CacheBlock block = new CacheBlock(recordNo, data, block2version, status );
                    if ( value.getData().length > block.getBlockSize() ) {
                        int size = findBlockSize( value.getData().length );
                        lock.lock();
                        try {
                            // delete current record first

                            // append next record at the end
                            block.setDataOffset(dataOffset);
                            block.setBlockSize( size);
                            block.setDataLen( value.getData().length);
                            dataOffset += size;
                        } finally {
                            lock.unlock();
                        }
                        try {
                            // don't call writeNewBlock
                            writeExistBlock(block);
                        } catch (IOException ex) {
                            logger.error( ex.getMessage(), ex);
                            throw new StoreException(ex.getMessage());
                        }
                    }
                    else {
                        try {
                            writeExistBlock(block);
                        } catch (IOException ex) {
                            logger.error( ex.getMessage(), ex);
                            throw new StoreException(ex.getMessage());
                        } finally {
                        }
                    }
                    return true;
                }
            }
            else {   //recordNo == totalRecord
                return populate( ks, value);
            }
        }
    }

    private CacheBlock makeBlock(long offset,int record, int blockSize, int dataLen, long version, short node) {
        //long data = (dataOffset << LEN | dataLen) ;
        long data = convertOffset4Len(offset, dataLen) ;
        //checkFileSize(list.get(curIndex).getDataOffset(), dataLen);
        long b2v = convertVersion4Size(version, blockSize) ;
        return new CacheBlock( record, data, b2v, (byte) 0,  node );
    }

    protected boolean populate(byte[] keys, Value<byte[]> value) {
        lock.lock();
        try {
            int blen = defineSize( value.getData().length );
            long keyOffset2Len = convertOffset4Len( keyOffset, keys.length);
            CacheBlock<byte[]> block = makeBlock( dataOffset, totalRecord, blen, value.getData().length, value.getVersion(), value.getNode());
            block.setValue( new SoftReferenceImpl( value.getData()));
            writeNewBlock(block, keyOffset2Len, keys);
            totalRecord ++;
            dataOffset += blen;
            keyOffset += keys.length;
            return true;
        } catch (Exception ex) {
            logger.error( ex.getMessage(), ex);
            return false;
        } finally {
            lock.unlock();
        }
    }

    public void writeExistBlock(CacheBlock<byte[]> block) throws IOException {
        ByteBuffer dataBuf = ByteBuffer.wrap( block.getData() );
        // don't use flip() before it write when use wrap
        //dataBuf.flip();
        dataChannel.write( dataBuf, block.getDataOffset() );
        long pos = OFFSET + (long) block.getRecordNo() * RECORD_SIZE;
        int off = 9;
        ByteBuffer buf = ByteBuffer.allocate( RECORD_SIZE - off);
        buf.putLong(block.getDataOffset2Len() );
        buf.putLong(block.getBlock2Version() );
        buf.putShort(block.getNode() );
        // must flip() before it write
        buf.flip();
        indexChannel.write( buf, pos + off);
    }


    private void writeNewBlock(CacheBlock<byte[]> block, long keyOffset2Len, byte[] key) throws IOException {
        // write in sequence of data, key and index channel, which doen not need call flip()
        ByteBuffer dataBuf = ByteBuffer.wrap(block.getData());
        dataChannel.write( dataBuf, block.getDataOffset() );
        // position to end of block
        if (block.getBlockSize() > block.getDataLen()  ) {
            ByteBuffer fillBuf = ByteBuffer.wrap( new byte[] {0} );
            dataChannel.write( fillBuf, block.getDataOffset()+ block.getBlockSize()-1 );
        }
        ByteBuffer keyBuf = ByteBuffer.wrap( key );
        keyChannel.write( keyBuf, getOffset(keyOffset2Len));
        writeIndexBlock(block, keyOffset2Len, indexChannel );
    }

    private void writeIndexBlock(CacheBlock<byte[]> block, long keyOffset2Len, FileChannel channel ) throws IOException {
        long pos = OFFSET + (long) block.getRecordNo() * RECORD_SIZE;
        checkFileSize(pos, RECORD_SIZE );
        ByteBuffer buf = ByteBuffer.allocate( RECORD_SIZE);
        buf.put(block.getStatus() );
        buf.putLong(keyOffset2Len );
        buf.putLong(block.getDataOffset2Len() );
        buf.putLong(block.getBlock2Version() );
        buf.putShort(block.getNode() );
        buf.flip();
        channel.write( buf, pos);
    }


    private int findBlockSize(int len) {
        int size= defineSize( len);
        if ( size < len ) {
            logger.warn("size "+ size+" should not be less than "+len+"  use "+len);
            size = len;
        }
        if ( size > MAX_BLOCK_SIZE ) {
            logger.error("defineSize " + size +" exceeding "+MAX_BLOCK_SIZE);
            throw new StoreException("defineSize " + size +" exceeding "+MAX_BLOCK_SIZE);
        }
        else return size ;
    }
    //make it thread save, that it did not curRecord
    protected Pair<byte[],byte[]> next(int current, ByteBuffer buf) throws IOException {
        buf.clear();
        long pos = OFFSET + (long) current * RECORD_SIZE;
        indexChannel.read(buf, pos);
        buf.rewind();
        byte status = buf.get();
        if ( isDeleted(status) ) {
            return null;
        }
        else {
            long keyLen = buf.getLong();
            byte[] keys = readChannel( keyLen, keyChannel);
            long data = buf.getLong();
            long block2version = buf.getLong();
            CacheBlock block = new CacheBlock(current, data, block2version, status );
            if ( block.getDataOffset() <= 0 || block.getDataLen() <= 0 || block.getBlockSize() < block.getDataLen() ) {
                throw new StoreException("data reading error");
            }
            else {
                //Key key = toKey(keys);
                byte[] datas = readChannel( block.getDataOffset2Len(), dataChannel);
                return new Pair( keys, datas);
            }
        }
    }
    //64MB size of block
    protected final int BLOCK_SIZE = 1 << 26;
    /**
     *
     * @return list of 2 integer,
     * 1st is totalRecord
     * 2nd is block size of record
     */
    public List<Integer> findTotal2BatchSize() {
        return getTotal2BatchSize(BLOCK_SIZE);
    }

    private List<Integer> getTotal2BatchSize(long size) {
        List<Integer> list = new ArrayList<Integer>(2);
        list.add(getTotalRecord() );
        try {
            long fileSize = dataChannel.size();
            int noBlock;
            if ( fileSize == 0)
                noBlock = 1;
            else {
                if (fileSize % BLOCK_SIZE == 0)
                    noBlock = (int) (fileSize / BLOCK_SIZE);
                else
                    noBlock = (int) (fileSize / BLOCK_SIZE) + 1;
            }

            //compute the number of record for block
            int block = getTotalRecord() / noBlock;
            list.add( block);
        } catch (IOException e) {
            logger.error( e.getMessage(), e);
            list.add( 2 << 12 );
        }
        return list;
    }

    public List<Long> findTotalBlock() {
        List<Long> list = new ArrayList<Long>();
        list.add( (long) getTotalRecord() );
        try {
            list.add( dataChannel.size());
        } catch (IOException e) {
            list.add(0L);
        }
        return list;
    }
}


