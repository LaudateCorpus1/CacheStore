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

package com.sm.store.loader;

import com.sm.message.Invoker;
import com.sm.store.Loader;
import groovy.lang.GroovyObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.concurrent.ConcurrentMap;


public class ThreadLoader implements Loader {
    private static Log logger = LogFactory.getLog(ThreadLoader.class);
    // Holding service instance
    final ExtendedGroovyLoader groovyLoader = new ExtendedGroovyLoader();
    public final static String STORE_MAPS ="storeMaps";
    final String scriptPath = System.getProperty("scriptPath", "./config/script")+"/";

    public ThreadLoader() {}

    @Override
    public Object invoke(ServiceClass service, Invoker invoker) {
        boolean isGroovy = ServiceClass.isGroovyScript( invoker.getClassName());
        try {
            if (isGroovy) {
                URL url = new URL ("file://"+service.getUrl());
                if (groovyLoader.isSourceChange(url, service.getInstance().getClass()) ) {
                    logger.info("source changed "+url);
                    //for groovy, className means script file name,
                    Class oldCls = service.getInstance().getClass();
                    GroovyObject oldObj = (GroovyObject) service.getInstance() ;
                    Class newCls= groovyLoader.recompile(url, oldCls.getName(), oldCls);
                    GroovyObject groovyObj = (GroovyObject) newCls.newInstance();
                    //move storeMaps form oldObj to groovyObj
                    groovyObj.getMetaClass().setProperty(groovyObj, STORE_MAPS,
                            oldObj.getMetaClass().getProperty(service.getInstance(), STORE_MAPS));
                    //change the instance with new groovy object
                    service.setInstance(groovyObj);
                    return groovyObj.invokeMethod(invoker.getMethod(), invoker.getParams());
                }
                else {
                    GroovyObject groovyObj = (GroovyObject) service.getInstance();
                    return groovyObj.invokeMethod(invoker.getMethod(), invoker.getParams());
                }
            } else {
                return service.getMethod().invoke(service.getInstance(), invoker.getParams());
            }
        } catch( Exception ex) {
          throw new RuntimeException( ex);
        }
    }


    public Object loadClass(ConcurrentMap<String, ServiceClass> serviceMap,String className) {
        boolean isGroovy = ServiceClass.isGroovyScript( className);
        try {
            logger.info("Put "+ className + " into serviceMap" );
            Object instance;
            String url = null;
            if ( isGroovy ) {
                //String scriptPath = System.getProperty("scriptPath", "./config/script")+"/";
                logger.info("script path "+scriptPath+" class name "+className );
                url = new File( scriptPath+className).getCanonicalPath();
                instance = loadGroovyClass(  new File( url));
                ServiceClass service = new ServiceClass(null, instance);
                service.setUrl( url);
                serviceMap.put( className, service);
            }
            else {
                instance = loadJavaClass(className);
                //serviceMap.put( name, instance );
                Method[] methods = instance.getClass().getDeclaredMethods();
                for (Method method : methods) {
                    if (serviceMap.containsKey(className + "." + method.getName()))
                        continue;
                    ServiceClass service = new ServiceClass(method, instance);
                    serviceMap.put(className + "." + method.getName(), service);
                    logger.info("Put " + className + "." + method.getName() + " into methodMap");
                }
            }
            logger.info("setting store nameMap for "+className);
            return instance;
        } catch (Exception ex) {
            throw new RuntimeException( ex.getMessage(), ex);
        }
    }

    private GroovyObject loadGroovyClass(File file) throws Exception {
        Class cls = groovyLoader.parseClass(file);
        GroovyObject groovyObj = (GroovyObject) cls.newInstance();
        return groovyObj;
    }

    private Object loadJavaClass(String className) throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Object instance;
        if (loader != null)
            instance = Class.forName(className, false, loader).newInstance();
        else
            instance = Class.forName(className).newInstance();
        return instance;

    }

    public Object loadTrigger(String className) throws Exception {
        boolean isGroovy = ServiceClass.isGroovyScript( className);
        if ( isGroovy) {
            //String scriptPath = System.getProperty("scriptPath", "./config/script")+"/"+className;
            logger.info("script path "+scriptPath+ " class name "+className );
            String url = new File( scriptPath+className).getCanonicalPath();
            Class cls = groovyLoader.parseClass( new File( url));
            return cls.newInstance();
        }
        else
            return loadJavaClass( className);
    }

    public Object reloadTrigger(String className, Object trigger) throws Exception {
        boolean isGroovy = ServiceClass.isGroovyScript( className);
        if ( isGroovy) {
            logger.info("reload script path "+scriptPath+ " class name "+className );
            String path = new File( scriptPath+className).getCanonicalPath();
            URL url = new URL("file://"+path);
            //old class is null, force it recompile
            Class cls = groovyLoader.recompile( url, trigger.getClass().getName(), null);
            return cls.newInstance();
        }
        else
            return loadJavaClass( className);
    }
}
