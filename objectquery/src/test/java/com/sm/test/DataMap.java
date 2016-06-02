package com.sm.test;

import com.sm.query.EstimateMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mhsieh on 2/1/16.
 */
public class DataMap implements EstimateMap {
    Map<String, Double> map = new HashMap<String, Double>();

    public void populateMap() {
        map.put("ncs", 1000.00);
        map.put("crm", 12000.00);
        map.put("foods.1", 0.1);
        map.put("foods.1.1", 0.1);
        map.put("foods.2", 0.2);
        map.put("beverages.1", 0.3);
        map.put("other.1", 0.2);
        map.put("other.2", 0.2);
        map.put("gender.male", 0.6);
        map.put("age.25-34", 0.5);
        map.put("uk.age.18-24", 0.3);
        map.put("ms.1.1", 0.3);
        map.put("ms", 2000.00);
        map.put("uk", 7000.00);
        map.put("ms.and.uk", 0.8);
        map.put("uk.and.ms", 0.8);
        map.put("uk.or.ms", 0.4);
        map.put("ms.or.uk", 0.4);
        map.put("us.age.18-24", 0.7);
        map.put("exp.1.1", 0.3);
        map.put("us.and.exp", 0.2);
        map.put("exp", 20000.00);
        ((Map)map).put("exp.and.src.us", "exp");
        ((Map)map).put("us.and.src.exp", "exp");
        ((Map)map).put("ncs.and.src.us", "ncs");
        ((Map)map).put("us.and.src.ncs", "ncs");
        ((Map)map).put("exp.or.src.us", "us");
        ((Map)map).put("ncs.or.src.us", "us");
        ((Map)map).put("exp.or.src.us", "us");
        ((Map)map).put("ncs.or.src.us", "us");
    }

    @Override
    public Double get(String key) {
        return map.get(key) ;
    }

    @Override
    public Object getObject(String key) {
        return ((Map) map).get(key) ;
    }
}
