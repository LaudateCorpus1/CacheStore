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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.utils.Pair;

import java.io.IOException;
import java.nio.ByteBuffer;

import static voldemort.store.cachestore.BlockUtil.*;


/**
 * Created by mhsieh on 2/13/16.
 */
public class MRStore extends ServerStore {
    private static Log logger = LogFactory.getLog(MRStore.class);
    public static final int BUF_SIZE = 8192;
    public static final int INDEX_SIZE = 27 * 303;
    protected ByteBuffer indexBuf = ByteBuffer.allocate(INDEX_SIZE);
    protected ByteBuffer keyBuf = ByteBuffer.allocate(BUF_SIZE);
    protected ByteBuffer dataBuf = ByteBuffer.allocate(BUF_SIZE);
    protected volatile long keyPos;
    protected volatile long dataPos;
    protected volatile long indexPos;


    public MRStore(String filename) {
        this(filename, false);
    }

    public MRStore(String filename, boolean flag) {
        super(filename, flag);
        //read into byteBuffer
        try {
            long pos = indexChannel.read(indexBuf, OFFSET);
            indexBuf.rewind();
            indexPos = pos + OFFSET ;
            pos = dataChannel.read( dataBuf, OFFSET);
            dataBuf.rewind();
            dataPos = pos + OFFSET;
            pos = keyChannel.read( keyBuf, OFFSET);
            keyBuf.rewind();
            keyPos = pos +OFFSET ;
        } catch (Exception ex) {
            throw new RuntimeException( ex.getMessage(), ex);
        }
    }

    /**
     * check index buffer has enough data, if not read next buffer
     * @return boolean
     */
    protected boolean checkIndexBuf() {
        long remain = indexBuf.remaining();
        if ( remain == 0) {
            indexBuf.clear();
            try {
                long pos = indexChannel.read(indexBuf, indexPos);
                indexPos += pos ;
                indexBuf.rewind();
                if (pos <= RECORD_SIZE && pos > 0 )
                    return false;
                else
                    return true;

            } catch (IOException ex) {
                logger.error( ex.getMessage());
                return false;
            }
        }
        else if ( remain >= RECORD_SIZE) {
            return true;
        }
        else {
            logger.error(remain+ " remaining < "+RECORD_SIZE);
            return false;
        }
    }

    /**
     * read next key buffer and update keyPos
     * @return length of buffer
     */
    protected long readKeyBuf() {
        keyBuf.clear();
        try {
            long len = keyChannel.read(keyBuf, keyPos);
            keyPos += len;
            keyBuf.rewind();
            return len;
        } catch (IOException ex) {
            throw new RuntimeException( ex.getMessage(), ex) ;
        }
    }

    /**
     * read next key bytes from key buffer
     * @param keyLen
     * @return
     */
    protected byte[] readKeyChannel(int keyLen) {
        long remain = keyBuf.remaining();
        byte[] keys = new byte[ keyLen];
        if ( remain == 0) {
            long len = readKeyBuf();
            if (len < keyLen )
                throw new RuntimeException("keyLen "+keyLen+" > read data len "+len);
            else {
                keyBuf.get(keys);
            }
        }
        else if ( remain >= keyLen) {
            keyBuf.get( keys);
        }
        else {
            byte[] ks = new byte[ (int) remain];
            keyBuf.get( ks);
            long len = readKeyBuf();
            if (len < keyLen - remain)
                throw new RuntimeException("keyLen " + keyLen + " > read data len " + len);
            else {
                byte[] ks2 = new byte[(int) (keyLen - remain)];
                keyBuf.get( ks2);
                System.arraycopy(ks,0, keys, 0,  ks.length );
                System.arraycopy(ks2, 0, keys, ks.length, ks2.length);
            }
        }
        return keys;
    }

    /**
     * read next data buffer, update dataPos
     * @return length of data buffer
     */
    protected long readDataBuf() {
        dataBuf.clear();
        try {
            long len = dataChannel.read(dataBuf, dataPos);
            dataPos += len;
            dataBuf.rewind();
            return len;
        } catch (IOException ex) {
            throw new RuntimeException( ex.getMessage(), ex) ;
        }
    }

    /**
     * when data block size > BUF_SIZE, pass the large byte array to be read
     * but not into data buffer which is 8192 bytes
     * @param bytes
     */
    protected void readDataBufAry(byte[] bytes) {
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap( bytes);
            long len = dataChannel.read(byteBuffer);
            dataPos += len;
            //return bytes;
        } catch (IOException ex) {
            throw new RuntimeException( ex.getMessage(), ex) ;
        }
    }

    /**
     * read destinate data len from data buffer and advance the gap (blocksize -d datalen)
     * also handle if block size > BUF_SIZE
     * @param dataLen
     * @param blockSize
     * @param offset
     * @return
     */
    protected byte[] readDataChannel(long dataLen, long blockSize, long offset){
        if ( offset > dataPos )
            logger.error("offset "+offset + " > dataPos "+dataPos);
        long remain = dataBuf.remaining();
        byte[] datas = new byte[(int) dataLen];
        if ( remain == 0) {
            //byte[] datas = new byte[(int) dataLen];
            //check block Size
            if ( blockSize < BUF_SIZE) {
                long len = readDataBuf();
                dataBuf.get(datas);
                if ( dataLen < blockSize)  {
                    byte[] rm = new byte[(int) (blockSize - dataLen)];
                    dataBuf.get( rm);
                }

            } else {  //create a byteBuff that match for block size
                readDataBufAry(datas);
                if ( blockSize > dataLen) {
                    byte[] rs = new byte[ (int) (blockSize -dataLen)];
                    readDataBufAry( rs);
                }
            }
            //return datas;
        }
        // need to spread
        else if ( remain >= blockSize) {
            //byte[] datas = new byte[(int) dataLen];
            dataBuf.get(datas);
            if ( dataLen < blockSize)  {
                byte[] rm = new byte[(int) (blockSize - dataLen)];
                dataBuf.get( rm);
            }
            //return datas;
        }
        else {   //remain < blockSize
            if ( remain >= dataLen) {
                //byte[] datas = new byte[ (int) dataLen];
                dataBuf.get( datas);
                long len = readDataBuf();
                byte[] ds = new byte[(int) (blockSize -remain)];
                dataBuf.get(ds);
                //return datas;
            }
            else {   //remain < dataLen
                byte[] ks = new byte[(int) remain];
                dataBuf.get(ks);
                if (blockSize - remain < BUF_SIZE) {
                    //byte[] datas = new byte[(int) dataLen];
                    readDataBuf();
                    byte[] ks2 = new byte[(int) (dataLen - remain)];
                    dataBuf.get(ks2);
                    System.arraycopy(ks, 0, datas, 0, ks.length);
                    System.arraycopy(ks2, 0, datas, ks.length, ks2.length);
                    if (dataLen < blockSize) {
                        byte[] rm = new byte[(int) (blockSize - dataLen)];
                        dataBuf.get(rm);
                    }
                    //return datas;
                } else {
                    byte[] blocks = new byte[(int) (blockSize - remain)];
                    readDataBufAry(blocks);
                    //byte[] datas = new byte[(int) dataLen];
                    System.arraycopy(ks, 0, datas, 0, ks.length);
                    System.arraycopy(blocks, 0, datas, ks.length, (datas.length - ks.length));
                    //return datas;
                }
            }
        }
        return datas;
    }

    //byte status;
    //protected byte[] rem = new byte[26]; // for delete record to advance 26 bytes
    protected byte[] r2 = new byte[2]; // for regular record to advance node Id 2 bytes
    @Override
    public Pair<Key,byte[]> next() throws IOException {
        if ( ! checkIndexBuf() )
            throw new RuntimeException("index buffer error");
        byte status = indexBuf.get();
        curRecord++ ;
        long keyPos = indexBuf.getLong();
        //long offset = getOffset( keyPos);
        int KeyLen = getLen( keyPos);
        byte[] ks = readKeyChannel( KeyLen);
        long dataOffset2Len = indexBuf.getLong();
        long block2version = indexBuf.getLong();
        indexBuf.get(r2);
        long len = getLen(dataOffset2Len);
        long offset = getOffset( dataOffset2Len);
        long blockSize = getSize( block2version);
        //logger.info("rec #"+curRecord+" offset "+offset+" len "+len + " block "+blockSize);
        Key key = toKey(ks);
        byte[] datas = readDataChannel( len, blockSize, offset );
        if ( isDeleted( status))
            return null;
        else
            return new Pair( key, datas);
    }

    @Override
    public void setCurrent(int current) {
        if ( current >= getTotalRecord() )
            throw new RuntimeException("current "+current+ " >= total "+getTotalRecord() );
        else {
            curRecord = current;
            if ( current == 0 ) return;
            //set 3 channel position
            long pos = OFFSET + (long) getCurRecord() * RECORD_SIZE;
            try {
                //read the first index record
                ByteBuffer buf = ByteBuffer.allocate(RECORD_SIZE);
                //re read the index buffer
                indexChannel.read(buf, pos);
                buf.rewind();
                long len = indexChannel.read(indexBuf, pos);
                indexBuf.rewind();
                indexPos = pos + len;
                // data and key pos, need to get from previous index record
                buf.get();  //get status byte
                long key = buf.getLong();
                long keyOff = getOffset( key);
                //long keyLen = getLen( key);
                len = keyChannel.read(keyBuf, keyOff );
                keyBuf.rewind();
                keyPos = keyOff + len;
                //read data channel
                long dataOffset2Len = buf.getLong();
                //long block2version = buf.getLong();
                long dataLen = getLen(dataOffset2Len);
                long offset = getOffset( dataOffset2Len) ;
                //long blockSize = getSize( block2version);
                len = dataChannel.read( dataBuf, offset);
                dataBuf.rewind();
                dataPos = offset + len ;
            } catch (IOException ex) {
                throw new RuntimeException( ex.getMessage(), ex);
            }
        }
    }
}
