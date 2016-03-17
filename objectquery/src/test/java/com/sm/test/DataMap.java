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

package com.sm.test;

import com.sm.query.EstimateMap;

import java.util.HashMap;
import java.util.Map;

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
    }

    @Override
    public Double get(String key) {
        return map.get(key) ;
    }
}
