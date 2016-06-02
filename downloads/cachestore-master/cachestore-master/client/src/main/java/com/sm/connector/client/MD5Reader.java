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

package com.sm.connector.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by mhsieh on 3/3/16.
 */
public class MD5Reader {
    private static Log logger = LogFactory.getLog(MD5Reader.class);

    public static final int BUF_SIZE = 8192;
    protected String filename;
    protected BufferedInputStream dataChannel;
    protected int size;
    public static final int SIZE = 32;
    protected long length ;
    protected long pos;
    protected boolean EOF = false;

    public MD5Reader(String filename) {
        this(filename, SIZE);
    }

    public MD5Reader(String filename, int size) {
        if ( size < 1 ) throw new RuntimeException("size must be > 0 ");
        this.filename = filename;
        this.size = size;
        init();
    }

    protected byte[] buf = new byte[BUF_SIZE];
    protected int bufLen, bufIndex ;
    protected void init() {
        try {
            File dataFile = new File(filename);
            dataChannel = new BufferedInputStream(new FileInputStream( dataFile), BUF_SIZE);
            length = dataFile.length();
            pos = 0 ;
            readBuf();
        } catch ( IOException ex) {
            throw new RuntimeException( ex.getMessage(), ex);
        }
    }

    private void readBuf() throws IOException {
        bufIndex = 0;
        bufLen = dataChannel.read(buf);
        //if ( bufLen < BUF_SIZE )
        //    logger.warn( "size "+bufLen +" < "+BUF_SIZE);
    }

    protected byte nextByte ;
    private void getByte() throws IOException {
        if ( bufIndex < bufLen ) {
            nextByte = buf[bufIndex++];
            pos++;
        }
        else {
            readBuf();
            if ( bufLen <= 0) {
                EOF = true;
            }
            else {
                nextByte = buf[bufIndex++];
                pos ++;
            }
        }

    }

    protected boolean useNext = false;
    public byte[] next() throws IOException {
        byte[] data = new byte[size];
        int i = 0;
        if ( useNext ) {
            data[ i++] = nextByte;
            useNext = false;
        }
        for ( ; ;  ) {
            getByte();
            if ( EOF )
                break;
            else {
                if ( nextByte == 10 || nextByte == 12) {
                    while ( true ) {
                        getByte();
                        if (EOF ) break;
                        if ( nextByte == 10 || nextByte == 12 )
                            continue ;
                        else {
                            useNext = true;
                            break;
                        }
                    }
                    break;
                }
                else {
                    if ( i < data.length)
                        data[i++] = nextByte;
                    else {
                        //expend the length
                        byte[] ds = new byte[ data.length * 2];
                        System.arraycopy(data, 0, ds, 0, data.length);
                        data = ds;
                        data[i++] = nextByte;
                    }
                }
            }
        }
        if ( i == size  )
            return data;
        else {
            byte[] bs ;
            if ( i > size )
                bs = new byte[i];
            else
                bs = new byte[i];
            System.arraycopy(data, 0, bs, 0, bs.length );
            return bs;
        }
    }

    public boolean hasNext() {
        if ( EOF || pos >= length)
            return false;
        else
            return true;
    }

    public void close() throws IOException {
        dataChannel.close();
    }

    public boolean isEnd(long end) {
        if ( EOF || pos >= end)
            return true;
        else
            return false;
    }

    public void setCurrent(long current) {
        if ( current <= 0 ) return;
        try {
            if ( current > BUF_SIZE ) { //need to read next buf
                dataChannel.skip(current -BUF_SIZE);
                pos = current;
                readBuf();
            }
            else {
                pos = current;
                bufIndex = (int) current;

            }
            next();
        } catch (IOException ex) {
            throw new RuntimeException( ex.getMessage(), ex);
        }
    }


    public long getLength() {
        return length;
    }

    public long getPos() {
        return pos;
    }
}
