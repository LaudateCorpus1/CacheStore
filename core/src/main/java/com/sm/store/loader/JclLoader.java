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

package com.sm.store.loader;

import com.sm.message.Invoker;
import com.sm.store.Loader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.xeustechnologies.jcl.JarClassLoader;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class JclLoader implements Loader {
    private static Log logger = LogFactory.getLog(JclLoader.class);
    private List<String> jarPathList;
   // private JarClassLoader jarClassLoader;
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();


    public JclLoader(List<String> jarPathList) {
        if ( jarPathList == null || jarPathList.size() == 0) throw  new RuntimeException("jarPathList can not empty");
        this.jarPathList = jarPathList;
        init();
    }


    private void init() {
//        this.jarClassLoader = new JarClassLoader();
//        for ( String  path : jarPathList ) {
//            jarClassLoader.add( path);
//        }
    }


    @Override
    public ServiceClass loadClass(ConcurrentMap<String, ServiceClass> serviceMap, String className) {
        return null;
    }

    @Override
    public Object invoke(ServiceClass serviceClass, Invoker invoker) {
        return null;
    }
}
