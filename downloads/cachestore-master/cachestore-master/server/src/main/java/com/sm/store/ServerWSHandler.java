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

package com.sm.store;

import com.sm.message.Invoker;
import com.sm.message.Response;
import com.sm.store.loader.ServiceClass;
import com.sm.store.loader.ThreadLoader;
import com.sm.store.server.RemoteStore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.annotations.jmx.JmxManaged;
import voldemort.annotations.jmx.JmxOperation;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import static com.sm.store.loader.ServiceClass.isGroovyScript;

@JmxManaged(description = "ServerWSHandler")
public class ServerWSHandler {
    private static Log logger = LogFactory.getLog(ServerWSHandler.class);

    // Holding service instance
    protected ConcurrentMap<String, ServiceClass> serviceMap = new ConcurrentHashMap<String, ServiceClass>();
    protected ConcurrentMap<String, RemoteStore> storeMaps;
    protected volatile Loader loader;

    public ServerWSHandler(ConcurrentMap<String, RemoteStore> storeMaps) {
        this.storeMaps = storeMaps;
        this.loader = new ThreadLoader();
    }

    public ServerWSHandler(ConcurrentMap<String, RemoteStore> storeMaps, Loader loader) {
        this.storeMaps = storeMaps;
        this.loader = loader;
    }

    public ConcurrentMap<String, RemoteStore> getStoreMaps() {
        return storeMaps;
    }

    public Loader getLoader() {
        return loader;
    }

    /**
      * look up service nameMap and invoke the corresponding method
      * @param invoker
      * @return response of result
      */
    public Response invoke(Invoker invoker) {
        if (! serviceMap.containsKey(getMapKey(invoker) )) {
            buildMap( invoker);
        }
        ServiceClass service = serviceMap.get( getMapKey(invoker));
        if ( service == null )
            return new Response("Can find method "+invoker.getMethod()+" in class "+invoker.getClassName(), true );
        else {
            try {
                logger.info("invoke "+invoker.getInvokerKey());
                Object payload = loader.invoke( service, invoker);
                return new Response( payload);
            } catch (Exception e) {
                logger.error( e.getMessage()+" "+invoker.toString(), e);
                return new Response(e.getMessage()+" "+invoker.getInvokerKey(), true);
            }
        }
    }

    private String getMapKey(Invoker invoker) {
        if ( isGroovyScript(invoker.getClassName()))
            return invoker.getClassName();
        else
            return invoker.getInvokerKey();
    }

    Lock lock = new ReentrantLock();
    private void buildMap(Invoker invoker) {
        lock.lock();
        try {
            // double check again, return if it is exist
            if ( serviceMap.containsKey( getMapKey(invoker) )) {
                return;
            }
            String name = invoker.getClassName();
            logger.info("Put "+ name + " into serviceMap" );
            loadClass( name );
        } catch (Exception ex) {
            throw new RuntimeException( ex.getMessage(), ex);
        } finally {
            lock.unlock();
        }
    }

    private void loadClass(String className) {
        logger.info("setting store nameMap for "+className);
        Object instance = loader.loadClass(serviceMap, className);
        ((StoreMap) instance).setStoreMap( storeMaps);
    }
    /*
       create a new class loader and new jar file will become effective
     */
    @JmxOperation(description = "resetLoader")
    public void resetLoader() {
        logger.info("clear serviceMap");
        lock.lock();
        try {
            serviceMap.clear();
        } catch (Exception ex) {
            throw new RuntimeException( ex.getMessage(), ex);
        } finally {
            lock.unlock();
        }
    }


}
