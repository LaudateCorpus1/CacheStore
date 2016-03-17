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

package com.sm.query.utils;

import com.sm.query.Result.Type;

public class ClassInfo {
    private String className;
    // store only field after process type
    // preprocess for performance tuning
    private FieldInfo[] fieldInfos;
    private Type type;
    public ClassInfo(String className, Type type, FieldInfo[] fieldInfos) {
        super();
        this.className = className;
        this.fieldInfos = fieldInfos;
        this.type = type;
    }
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public FieldInfo[] getFieldInfos() {
        return fieldInfos;
    }
    public void setFieldInfos(FieldInfo[] fieldInfos) {
        this.fieldInfos = fieldInfos;
    }
    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }

}
