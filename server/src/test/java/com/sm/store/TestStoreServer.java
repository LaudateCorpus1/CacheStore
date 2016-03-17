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

package com.sm.store;

import com.sm.store.server.RemoteStoreServer;
import com.sm.utils.JmxService;
import com.sm.utils.TupleThree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

import static com.sm.transport.Utils.getOpts;


public class TestStoreServer {
    private static final Log logger = LogFactory.getLog(TestStoreServer.class);

    public static void main(String[] args) {
        String[] opts = new String[] {"-store","-path", "-port", "-mode","-delay"};
        String[] defaults = new String[] {"store","./data", "7100", "0", "false" };
        String[] paras = getOpts( args, opts, defaults);
        String p0 = paras[0];
        int port = Integer.valueOf( paras[2]);
        String path = paras[1];
        int mode = Integer.valueOf(paras[3]);
        boolean delay = Boolean.valueOf(paras[4]);
        String[] stores = p0.split(",");
        List<TupleThree<String, String, Integer>> storeList = new ArrayList<TupleThree<String, String, Integer>>();
        for ( String store : stores) {
            storeList.add( new TupleThree<String, String, Integer>( store, path, mode));

        }
        logger.info("start server at "+port);
        RemoteStoreServer rs = new RemoteStoreServer(port, storeList, delay);
        logger.info("hookup jvm shutdown process");
        rs.hookShutdown();
        List list = new ArrayList();
        //add jmx metric
        for (String store : stores ) {
            list.add(rs.getRemoteStore(store));
        }
        list.add(rs);
        JmxService jms = new JmxService( list);
    }
}
