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

package com.sm.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.utils.JmxUtils;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JmxService {
    private static final Log logger = LogFactory.getLog(JmxService.class);

    private List serviceList;
    private final Object lock = new Object();
    private final MBeanServer mbeanServer;
    private final Set<ObjectName> registeredBeans;

    public JmxService(List serviceList) {
        this(serviceList, null);
    }

    public JmxService(List serviceList, List<String> names) {
        if ( serviceList == null || serviceList.size() == 0 )
            throw new RuntimeException("serviceList is null or empty");
        this.serviceList = serviceList;
        this.mbeanServer = ManagementFactory.getPlatformMBeanServer();
        this.registeredBeans = new HashSet<ObjectName>();
        if ( names == null)
            start();
        else
            start(names);
    }

    public void start(List<String> names) {
        for ( int i =0; i < serviceList.size(); i++) {
            ObjectName objectName = JmxUtils.createObjectName(serviceList.get(i).getClass().getName(), names.get(i));
            registerBean( serviceList.get(i), objectName);
            this.registeredBeans.add( objectName);
        }
    }

    public void start() {
        for (Object each : serviceList ) {
            ObjectName objectName = JmxUtils.createObjectName(each.getClass());
            registerBean( each, objectName);
            this.registeredBeans.add( objectName);
        }
    }

    public void stop() {
        for ( ObjectName each : registeredBeans ) {
            JmxUtils.unregisterMbean(mbeanServer, each);
        }
    }


    private void registerBean(Object o, ObjectName name) {
        synchronized(lock) {
            try {
                if(mbeanServer.isRegistered(name)) {
                    logger.warn("Overwriting mbean " + name);
                    JmxUtils.unregisterMbean(mbeanServer, name);
                }
                JmxUtils.registerMbean(mbeanServer, JmxUtils.createModelMBean(o), name);
                this.registeredBeans.add(name);
            } catch(Exception e) {
                logger.error("Error registering bean with name '" + name + "':", e);
            }
        }
    }

}
