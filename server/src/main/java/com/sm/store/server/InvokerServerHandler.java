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

package com.sm.store.server;


import com.sm.localstore.impl.HessianSerializer;
import com.sm.message.Header;
import com.sm.message.Invoker;
import com.sm.message.Request;
import com.sm.message.Response;
import com.sm.storage.Serializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import voldemort.store.cachestore.StoreException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InvokerServerHandler extends SimpleChannelUpstreamHandler {
    private static final Log logger = LogFactory.getLog(InvokerServerHandler.class);

    private ConcurrentMap<String, Service> serviceMap;
    // configuratio instamce
    private Serializer serializer;
    // list of classes that implements ineterface
    private List<String> classList;

    public InvokerServerHandler(List<String> classList) {
        this(null, classList);
    }

    public InvokerServerHandler(Serializer serializer, List<String> classList) {
        this.serializer = serializer;
        if ( serializer == null ) serializer = new HessianSerializer();
        this.classList = classList;
        if ( classList == null || classList.size() == 0 ) throw new RuntimeException("classList is null or freq 0");
        this.serviceMap = new ConcurrentHashMap<String, Service>(19);
        init();

    }

    private void init() {
        for (String each : classList ) {
            Object instance = loadClass(each);
            //serviceMap.put( name, instance );
            Method[] methods = instance.getClass().getMethods();
            for ( Method method : methods ) {
               Service service = new Service(method, instance);
               serviceMap.put( each+"."+method.getName(), service);
               logger.info("Put "+each+"."+method.getName()+" into methodMap" );
            }

            }
    }


    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
      super.handleUpstream(ctx, e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        Request req =  (Request) e.getMessage();
        logger.info("receive " + req.toString()+" from "+e.getRemoteAddress().toString());
        // it might need to create a different copy
        Header header = new Header( req.getHeader().getName(), req.getHeader().getVersion(), req.getHeader().getRelease(),
              req.getHeader().getNodeId());
        Response response = null;
        try {
          Invoker invoker = (Invoker) serializer.toObject( req.getPayload() );
          response = invoke( invoker );
        } catch (Exception ex) {
          logger.error(ex.getMessage(), ex);
          response = new Response( ex.getMessage(), true);
        } finally {
           byte[] payload = serializer.toBytes( response);
           Request request = new Request(header, payload ,  Request.RequestType.Response  );
           ctx.getChannel().write(request);
        }
    }



    /**
      * look up service serviceMap and invoke the corresponding method
      * @param invoker
      * @return response of result
      */
     private Response invoke(Invoker invoker) {
         Service service = serviceMap.get( invoker.getClassName()+"."+invoker.getMethod() );
         if ( service == null )
             return new Response("Can find method "+invoker.getMethod()+" in class "+invoker.getClassName(), true );
         else {
             try {
                 Object payload = service.method.invoke( service.instance, invoker.getParams());
                 return new Response( payload);
             } catch (Exception e) {
                 logger.error( e.getMessage(), e);
                 return new Response(e.getMessage()+" method "+invoker.getMethod()+" in class "+invoker.getClassName(), true);
             }
         }

     }



    private Object loadClass(String className) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            if (loader != null)
                return Class.forName(className, false, loader).newInstance();
            else
                return Class.forName(className).newInstance();
        } catch (Exception e) {
            throw new StoreException( e.getMessage(), e);
        }
    }

    class Service {
        Method method;
        Object instance;
        public Service(Method method, Object instance) {
            this.method = method;
            this.instance = instance;
        }
    }


}
