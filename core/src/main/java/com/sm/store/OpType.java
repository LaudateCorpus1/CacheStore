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
import voldemort.store.cachestore.StoreException;

public enum OpType {
    Get ((byte) 1), Put ((byte) 2 ), Remove ((byte) 3), GetKeyIterator ((byte) 4), Scan ((byte) 5), Update((byte) 6),
    MultiGets ((byte) 7), GetSeqNo ((byte) 8), GetSeqNoInt ((byte) 9), GetSeqNoBlock ((byte) 10),
    GetSeqNoBlockInt ((byte) 11), Insert ((byte)12),  MultiPuts ((byte) 13), SelectQuery ((byte) 14),
    UpdateQuery ((byte) 15), MultiSelectQuery ((byte) 16), MultiUpdateQuery ((byte) 17), MultiRemoves ((byte) 18),
    QueryStatement ((byte) 19);

    final byte value;

    OpType( byte value) {
        this.value = value;
    }

    public static OpType getOpType(byte value) {
        switch (value ) {
            case 1 : return Get;
            case 2 : return Put;
            case 3 : return Remove;
            case 4 : return GetKeyIterator ;
            case 5 : return Scan;
            case 6 : return Update;
            case 7 : return MultiGets;
            case 8 : return GetSeqNo;
            case 9 : return GetSeqNoInt;
            case 10 : return GetSeqNoBlock;
            case 11 : return GetSeqNoBlockInt;
            case 12 : return Insert;
            case 13 : return MultiPuts;
            case 14 : return SelectQuery;
            case 15 : return UpdateQuery;
            case 16 : return MultiSelectQuery;
            case 17 : return MultiUpdateQuery;
            case 18 : return MultiRemoves;
            case 19 : return QueryStatement;
            default: throw new StoreException("wrong op type value "+ value);
        }
    }

}

