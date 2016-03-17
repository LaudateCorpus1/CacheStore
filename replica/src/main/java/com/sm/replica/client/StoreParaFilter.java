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

package com.sm.replica.client;

import com.sm.replica.Filter;
import com.sm.store.OpType;
import com.sm.store.StoreParas;

import java.util.ArrayList;
import java.util.List;

public class StoreParaFilter implements Filter {
    //filter opType
    private OpType opType;

    public StoreParaFilter(OpType opType) {
        this.opType = opType;
    }

    @Override
    public List<StoreParas> applyFilter(List<StoreParas> list) {
        if ( opType == null )  return list;
        else {
            List<StoreParas> toReturn = new ArrayList<StoreParas>( list.size());
            for ( StoreParas each : list ) {
                //add to toReturn when opType is not filtered
                if ( each.getOpType() != opType )
                    toReturn.add( each);
            }
            return toReturn;
        }
    }
}

