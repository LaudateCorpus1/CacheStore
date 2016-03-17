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
package com.sm.message;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class Invoker implements Serializable {
    private static final long serialVersionUID = 1L;
    String className;
    String method;
    Object[] params;

    public Invoker(String className, String method, Object[] params) {
        this.className = className;
        this.method = method;
        this.params = params;
    }

    public String getInvokerKey() {
        return getClassName()+"."+getMethod();
    }

    public String getClassName() {
        return className;
    }

    public String getMethod() {
        return method;
    }

    public Object[] getParams() {
        return params;
    }

    public void addParas(Object para) {
        if ( params == null ) params = new Object[]{};
        List<Object> list = new ArrayList<Object>( params.length+ 1);
        for (int i = 0 ; i < params.length ; i++) {
            list.add(params[i]);
        }
        list.add(para);
        params = list.toArray();
    }

    public String toString() {
        return className+ "->"+ method + " params size " + ( params == null ? "null" : params.length) ;
    }

}
