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

import com.caucho.hessian.io.External;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public class StoreParaList implements Serializable, External {
    private OpType opType;
    private List<StoreParas> list;

    public StoreParaList() {
        super();
    }

    public StoreParaList(OpType opType, List<StoreParas> list) {
        this.opType = opType;
        this.list = list;
    }

    public OpType getOpType() {
        return opType;
    }

    public List<StoreParas> getList() {
        return list;
    }

    public int size() {
        if ( list == null ) return 0;
        else return list.size();
    }


    @Override
    public void writeExternal(Hessian2Output out) throws IOException {
        out.writeInt(opType.value);
        if ( size() == 0)
            out.writeBoolean(true);
        else {
            out.writeBoolean( false);
            out.writeInt(size());
            for (StoreParas each : list ) {
                //out.writeBytes( each.toBytes());
                out.writeObject( each);
            }
        }
    }

    @Override
    public void readExternal(Hessian2Input in) throws IOException {
        opType = OpType.getOpType( (byte) in.readInt());
        boolean isNull = in.readBoolean();
        if ( ! isNull ) {
            int size = in.readInt();
            list = new ArrayList<StoreParas>(size);
            for ( int i = 0; i < size ; i++) {
                System.out.println("j= "+i);
                StoreParas paras = (StoreParas) in.readObject();
                //StoreParas paras = StoreParas.toStoreParas(in.readBytes());
                list.add( paras);
            }
        }
    }
}
