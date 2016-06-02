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
 */package com.sm.store.client;

import com.sm.localstore.impl.HessianSerializer;
import com.sm.storage.Serializer;
import com.sm.store.client.grizzly.ClusterClientFilter;
import com.sm.store.client.netty.ClusterClientHandler;
import com.sm.store.cluster.Connection;
import com.sm.transport.Client;
import com.sm.transport.grizzly.codec.RequestCodecFilter;
import org.glassfish.grizzly.filterchain.BaseFilter;

public class TCPClientFactory {

    public static enum ClientType  {
        Netty ((byte) 1), Grizzly ((byte) 2) ;
        final byte value;
        ClientType(byte value) {
            this.value = value;
        }
        public static ClientType getClientType(byte value) {
            switch ( value ) {
                case 0 : return Netty;
                case 1 : return Netty;
                case 2 : return Grizzly;
                default: return Netty;
            }
        }

    }
    public static final Serializer EmbeddedSerializer = new HessianSerializer();
    public static Client createClient(ClientType clientType, String url, long timeout) {
        String[] strs = url.split(":");
        if ( strs.length != 2 ) throw new RuntimeException("malform url "+url);
        if ( clientType == ClientType.Netty ) {
            ClusterClientHandler handler =  new ClusterClientHandler(timeout, EmbeddedSerializer);
            return com.sm.transport.netty.TCPClient.start(strs[0], Integer.valueOf(strs[1]), handler, (byte) 1);
        }
        else {
            BaseFilter requestCodecFilter = new RequestCodecFilter( (byte) 1);
            return com.sm.transport.grizzly.TCPClient.start(strs[0], Integer.valueOf(strs[1]), requestCodecFilter,
                    new ClusterClientFilter(timeout) );
        }
    }

    public static Client createClient(ClientType clientType, Connection connection, long timeout) {

        if ( clientType == ClientType.Netty ) {
            ClusterClientHandler handler =  new ClusterClientHandler(timeout, EmbeddedSerializer);
            return com.sm.transport.netty.TCPClient.start(connection.getHost(), connection.getPort(), handler, (byte) 1);
        }
        else {
            BaseFilter requestCodecFilter = new RequestCodecFilter( (byte) 1);
            return com.sm.transport.grizzly.TCPClient.start(connection.getHost(), connection.getPort(),  requestCodecFilter,
                    new ClusterClientFilter(timeout) );
        }
    }

}
