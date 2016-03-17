/*
 *
 *
 * Copyright 2012-2015 Viant.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 *
 */

package com.sm.transport;
import com.sm.message.Response;
import com.sm.transport.netty.Decoder;
import com.sm.transport.netty.Encoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.util.concurrent.ThreadPoolExecutor;

public class Utils {
    private static final Log logger = LogFactory.getLog(Utils.class);

    // max len for 64 MB size
    public static final int MAX_LEN = 1 << 26;
    public final static byte SIGNATURE = (byte) 0xCA;
    // signature + version
    public final static int HEADER_SIZE = 2;
    public final static int LENGTH_OFFSET = HEADER_SIZE;
    // byte of int
    public final static int LENGTH_SIZE = 4;
    // short for request header
    public final static int H_SIZE = 2;
    public final static int R_SIZE = 1;
    // total 8 bytes + request type
    public final static int TOTAL = HEADER_SIZE + LENGTH_SIZE + H_SIZE ;
    public final static int OFFSET = HEADER_SIZE + LENGTH_SIZE;


    public enum SerializerType { Hessian((byte) 0),  Json( (byte) 0X40) ;
        final byte value;

        SerializerType(byte value) {
            this.value = value;
        }

        public static SerializerType getSerializerType(byte value) {
            switch (value) {
                case 0 : return Hessian;
                case 40 : return Json;
                default: return Hessian;
            }
        }
    }


    public static Decoder createDecoder(SerializerType type) {
        return new Decoder( type.value );
    }


    public static Encoder createEncoder(SerializerType type) {
        return new Encoder( type.value);
    }


    public enum ServerState { BootStrap ((int) 0), Active ((int) 1) , Suspend ((int) 2) , Shutdown ( (int) 3)  ;
        final int value;

        ServerState(int value) {
            this.value = value;
        }


        public static ServerState getServerState(int value) {
            switch( value) {
                case 0 : return BootStrap;
                case 1 : return Active;
                case 2 : return Suspend;
                case 3 : return Shutdown;
                default: return Active;
            }
        }

   }

    public static int match(String arg, String[] opts) {
        for ( int i = 0; i < opts.length ; i ++) {
            if ( arg.equals(opts[i]) )  return i;
        }
        return -1;
    }

    public static void help(String[] opts, String[] defaults ) {
        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < opts.length ; i ++ )
        {
            sb.append( ( i < opts.length ? opts[i] :" " ) + " " + ( i < defaults.length ? defaults [i]: " ") );
        }
        System.out.println("parameters "+ sb.toString() );
    }

    public static String[] getOpts(String[] args, String[] opts, String[] defaults) {
        if ( args.length == 0 || opts.length == 0  ) return defaults;
        for (int i =0 ; i < args.length ; i = i+ 2 ) {
            if ( args[i].equals("-help") || args[i].equals("-h"))  {
                help(opts, defaults );
                System.exit(1 );
            }
            else {
                int j = match( args[i], opts);
                if ( j >= 0 && j < defaults.length ) {
                     defaults[j] = args[ i+1 ];
                     //i += 2;
                    //remove leading -
                    if ( args[i].length() > 1) {
                        String key = args[i].substring(1);
                        //add opts into System Properties
                        System.setProperty(key, opts[j]);
                        logger.info("put "+key+ " = "+args[j]+ " into System property");
                    }
                }
                else {
                    System.out.println("opts "+args[i]+" is not support");
                    help(opts, defaults );
                    System.exit(1 );
                }

            }
        }
        return defaults;
    }

    private static final String	DOT	= "\\.";
    /**
     *
     * @param host address as String
     * @return ip as int
     */
    public static int getIP(String host){
        String[] ips = host.split(DOT);
        if ( ips.length != 4 ) {
            throw new RuntimeException("wrong host address "+host);

        }
        else {
            int toReturn = 0;
            for (int i = 0; i < ips.length; i++) {
                int no = 8 * (3 - i);
                toReturn += Integer.parseInt(ips[i]) << no;
            }
            return toReturn;
        }
    }

    /**
     *
     * @return Process ID
     */
    public static int getPID() {
        String str = ManagementFactory.getRuntimeMXBean().getName();
        String pid = str.substring(0, str.indexOf("@") );
        return Integer.valueOf( pid);
    }

    /**
     *
     * @return long represent by HostAddress+ PID
     */
    public static long getDefaultNodeId() {
        String host = null;
        try {
            host = Inet4Address.getLocalHost().getHostAddress();
        } catch (java.net.UnknownHostException  ex) {
            logger.error( ex.getMessage()+" using 127.0.0.1");
            host = "127.0.0.1";
        }
        int ip = getIP( host);
        logger.info("pid "+ getPID()+" ip "+ip +" host "+host);
        long toReturn = (((long) getPID()) << 32) + ip;
        return toReturn;
    }


    public static long getNodeId(String host) {
        int ip = getIP(host);
        long toReturn = (((long) getPID()) << 32) + ip;
        return toReturn;
    }


    public static byte[] response2Bytes(Response response) {
        if ( response.getPayload() instanceof  String ) {
            try {
                byte[] d = ( (String) response.getPayload()).getBytes("UTF-8") ;
                byte[] toReturn = new byte[ d.length + 1];
                toReturn[0] = (byte) ( response.isError() ? 1 : 0);
                System.arraycopy(d, 0, toReturn, 1, d.length );
                return toReturn;
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage() );
                return new byte[] { (byte) ( response.isError() ? 1 : 0) } ;
            }
        }
        else {
            return new byte[] { (byte) ( response.isError() ? 1 : 0) } ;
        }
    }

   public static boolean isOverLoaded(ThreadPoolExecutor pools) {
         int remain = pools.getQueue().remainingCapacity();
         int active =  pools.getActiveCount();
         if ( remain < active  ) {
             logger.info("Remaining capacity "+ remain +  " is less than active threads " + active);
             return true;
         }
         else return false;
    }

    public static Client getTCPClient(String[] urlArray) {
        return null;
    }
}
