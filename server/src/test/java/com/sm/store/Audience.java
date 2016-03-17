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

import com.sm.localstore.impl.HessianSerializer;
import com.sm.query.EstimateMap;
import com.sm.query.PredicateEstimator;
import com.sm.storage.Serializer;
import com.sm.store.server.RemoteStore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import voldemort.store.cachestore.Key;
import voldemort.store.cachestore.Value;

import java.util.concurrent.ConcurrentMap;


public class Audience implements StoreMap, EstimateMap {
    protected static final Log logger = LogFactory.getLog(Audience.class);
    private ConcurrentMap<String, RemoteStore> storeMaps;
    private RemoteStore remoteStore;
    public final static String ESTIMATE ="estimateStore" ;
    private String storeName ;
    private Serializer serializer = new HessianSerializer();

    public Audience() {
        this.storeName = "audienceBuilder";
    }

    @Override
    public void setStoreMap(ConcurrentMap<String, RemoteStore> storeMaps) {
        this.storeMaps = storeMaps;
        this.remoteStore = storeMaps.get(storeName);
        if (remoteStore == null ) {
            String error =  "can not find store "+storeName+" check the setting of estimateStore in config file" ;
            logger.error(error);
            throw new RuntimeException(error);
        }
    }

    /**
     *
     * @param predicate represent the predicate String
     * @param store  to use for population, like ncs, userCrm
     * @return
     */
    public long findEstimate(String predicate, String store) {
        PredicateEstimator predicateEstimator = new PredicateEstimator( this);
        double rate = predicateEstimator.runEstimate( predicate);
        if ( rate <= 0 ) {
            logger.warn(predicate +" return negative "+rate) ;
            rate = 0 ;
        }
        Double p = get(store) ;
        double population = -1  ;
        if ( p != null)
            population = p;
        logger.info("population "+population+ " rate "+rate+ " for "+predicate+" store "+store) ;
        return Math.abs(Math.round(population*rate));
    }

    public Double get(String key) {
        Value value = remoteStore.get(Key.createKey(key));
        if ( value == null) return null;
        else
            return (Double) serializer.toObject(((byte[])value.getData()));
    }

}

