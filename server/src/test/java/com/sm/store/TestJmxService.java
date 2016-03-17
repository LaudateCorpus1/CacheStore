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

import java.lang.ref.SoftReference;

public class TestJmxService {

    public static void main(String[] args) {
        SoftReference st = new SoftReference(null);
        System.out.println(st.get());
        String pre = "inprocess";
        String str = "inprocess/test/copy/";
        int no = str.lastIndexOf("/")+1;
        System.out.println(str.substring( str.indexOf(pre)+pre.length(), no));
        System.out.println( str.indexOf(pre));
        System.out.println( str.substring(no));


    }
//        @Test(groups = {"JmxService"})
//    public void testService() throws Exception {
//        RemoteStore remote = new RemoteStore("./path/test", new HessianSerializer());
//
//        List list = new ArrayList();
//        list.add(remote);
//        JmxService jms = new JmxService( list);
//        //Thread.sleep( 1000L * 2);
//        jms.stop();
//    }

}

