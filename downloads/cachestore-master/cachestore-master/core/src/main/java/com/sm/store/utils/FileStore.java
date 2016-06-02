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

package com.sm.store.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.*;
import voldemort.store.cachestore.impl.CacheValue;
import voldemort.store.cachestore.impl.SoftReferenceImpl;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static voldemort.store.cachestore.BlockUtil.*;

public class FileStore {
    private static Log logger = LogFactory.getLog(FileStore.class);

    private String path ;
    private String filename ;

    public final static int MAGIC = 0xBABECAFE;
    // beginning of index channel
    public final static int OFFSET = 4;
    //private CacheStore store;
    private FileChannel dataChannel;
    // key for map reside, it is write once
    private FileChannel keyChannel;
    // fixed len index block for dataChannel and keyChannel
    private FileChannel indexChannel;
    private volatile int totalRecord = 0 ;
    // current offset of dataChannel
    private volatile long dataOffset = 0;
    // key channel offset
    private volatile long keyOffset = 0;
    private BlockSize blockSize;

    public FileStore(String path, String filename) {
        this(path+"/"+filename);
    }

    public FileStore(String filename) {
        this.filename = filename;
        init();
    }

    public FileStore(String filename, BlockSize blockSize)  {
        this(filename);
        this.blockSize = blockSize;
    }


    private void init() {
        try {
            String index = "0";
            indexChannel = new RandomAccessFile( filename+index+".ndx", "rw").getChannel();
            checkSignature( indexChannel);
            keyChannel = new RandomAccessFile( filename+index+".key", "rw").getChannel();
            checkSignature( keyChannel);
            dataChannel = new RandomAccessFile( filename+index+".data", "rw").getChannel();
            checkSignature( dataChannel);
            long length = indexChannel.size() - OFFSET ;
            totalRecord = (int) ( length / RECORD_SIZE) ;
            if ( totalRecord > 0) {
                logger.info("Total record "+totalRecord+" move to eof and ready for append");
                reset();
            }
            dataOffset = dataChannel.size();
            keyOffset = keyChannel.size();
            logger.info("data "+dataOffset+"  index "+indexChannel.position()+" key "+keyOffset);
        } catch (Exception e) {
            throw new RuntimeException( e.getMessage(), e);
        }
    }

    private void reset() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(RECORD_SIZE);
        long pos = OFFSET + (long) (totalRecord-1) * RECORD_SIZE;
        indexChannel.read(buf, pos);
        buf.rewind();
        byte status = buf.get();
        long keyLen = buf.getLong();
        byte[] keys = readChannel( keyLen, keyChannel);
        long data = buf.getLong();
        long block2version = buf.getLong();
        CacheBlock block = new CacheBlock(totalRecord, data, block2version, status );
        byte[] datas = readChannel( block.getDataOffset2Len(), dataChannel);

    }

    private byte[] readChannel(long offset2len, FileChannel channel)  throws IOException {
        long offset = getOffset( offset2len);
        int len = getLen( offset2len);
        ByteBuffer data = ByteBuffer.allocate( len);
        channel.read( data, offset);
        return data.array();
    }

    private boolean checkSignature(FileChannel channel) throws IOException {
        ByteBuffer intBytes = ByteBuffer.allocate(OFFSET);
        if ( channel.size() == 0) {
            intBytes.putInt(MAGIC);
            intBytes.flip();
            channel.write(intBytes);
            return true;
        }
        else {
            channel.read(intBytes);
            intBytes.rewind();
            if ( intBytes.getInt() != MAGIC )
                throw new StoreException("Header mismatch expect "+MAGIC+" read "+ intBytes.getInt() );
        }
        return true;
    }


    private CacheBlock makeBlock(long offset,int record, int blockSize, int dataLen, long version, short node) {
        //long data = (dataOffset << LEN | dataLen) ;
        long data = convertOffset4Len(offset, dataLen) ;
        //checkFileSize(list.get(curIndex).getDataOffset(), dataLen);
        long b2v = convertVersion4Size(version, blockSize) ;
        return new CacheBlock( record, data, b2v, (byte) 0,  node );
    }

    protected int findSize(int len) {
        if (blockSize == null)
            return defineSize(len );
        else
            return blockSize.defineSize( len);
    }

    public boolean populate(byte[] keys, byte[] value) {
        try {
            long version = 1;
            int blen = findSize(value.length);   //defineSize( value.length );
            //byte[] keys = toKeyBytes( Key.createKey(new ByteArray(longToBytes(i))) );
            long keyOffset2Len = convertOffset4Len( keyOffset, keys.length);
            Value<byte[]> v = CacheValue.createValue(value, version);
            //block.setData( value.getData());
            CacheBlock<byte[]> block = makeBlock( dataOffset, totalRecord, blen, value.length , version, (short) 0);
            //block.setData( v.getData());
            block.setValue( new SoftReferenceImpl( v.getData()));
            writeNewBlock(block, keyOffset2Len, keys);
            totalRecord ++;
            dataOffset += blen;
            keyOffset += keys.length;
//            channel.setTotalRecord(channel.getTotalRecord()+1);
//            channel.setDataOffset( channel.getDataOffset() + blen);
//            channel.setKeyOffset( channel.getKeyOffset() + keys.length );
            return true;
        } catch (Exception ex) {
            logger.error( ex.getMessage(), ex);
            return false;
        }
    }

    private void writeNewBlock(CacheBlock<byte[]> block, long keyOffset2Len, byte[] key) throws IOException {
        // write in sequence of data, key and index channel, which doen not need call flip()
        ByteBuffer dataBuf = ByteBuffer.wrap( block.getData() );
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

    public boolean writeRecord(Key key, byte[] value) {
        return  populate( toKeyBytes( key), value);
    }


    public void close() {
        try {
            indexChannel.close();
            keyChannel.close();
            dataChannel.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
