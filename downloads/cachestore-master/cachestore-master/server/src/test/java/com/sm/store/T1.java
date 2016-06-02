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
 */package com.sm.store;

import com.sm.localstore.impl.HessianSerializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;

public class T1 implements Serializable {
   private static Log logger = LogFactory.getLog(T1.class);
    String s1;
    int i1;
    T2 t2;
    Integer l1;

    public T1() {
        super();
    }

    public T1(String s1, int i1, Integer l1, T2 t2) {
        this.s1 = s1;
        this.i1 = i1;
        this.t2 = t2;
        this.l1 = l1;
    }

//    //@Override
//    public void writeExternal(Hessian2Output out) throws IOException {
//        out.writeString(s1);
//        out.writeInt(i1);
//        //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    //@Override
//    public void readExternal(Hessian2Input in) throws IOException {
//        s1 = in.readString();
//        i1 = in.readInt();
//
//        //To change body of implemented methods use File | Settings | File Templates.
//    }

    static class T2 implements Serializable {
        String s2;
        int i2;

        T2(String s2, int i2) {
            this.s2 = s2;
            this.i2 = i2;
        }
    }

    public static void putInt(byte[] b, int off, int val) {
        b[off + 3] = (byte) (val >>> 0);
        b[off + 2] = (byte) (val >>> 8);
        b[off + 1] = (byte) (val >>> 16);
        b[off + 0] = (byte) (val >>> 24);
    }

    public static void main(String[] args) {
        int i = -1, j = - (0xFF), k = 0xff;
        byte[] bs = new byte[4];
        putInt(bs, 0, i);
        putInt(bs, 0, j);
        putInt(bs, 0, k);

        T1.T2 t2 = new T1.T2("test-10", 20);
        T1 t1 = new T1("test-1",1 , new Integer(4), t2);
        HessianSerializer<T1> hs = new HessianSerializer<T1>();
        String str = new String(hs.toBytes(t1));
        System.out.println( str);
        t1 =  new T1("test-1",1 , new Integer(4), null);
        str = new String(hs.toBytes(t1));
        System.out.println( str);
//        Constructor[] cs = t1.getClass().getConstructors();
//        for (Constructor each : cs) {
//            Type[] ts= each.getParameterTypes();
//            logger.info(ts.getClass().getName());
//            for ( Type type : ts)
//                logger.info(type.getClass().toString());
//        }
    }
}
