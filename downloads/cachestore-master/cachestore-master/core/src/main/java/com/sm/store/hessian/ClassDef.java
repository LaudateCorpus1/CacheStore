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

package com.sm.store.hessian;

import com.sm.store.hessian.PhpTokenizer.PhpDataType;

import java.util.List;

public class ClassDef {
    String name;
    List<FieldDef> fields;
    PhpDataType type;
    int refNo;
    boolean firstVisit = true;


    ClassDef(String name, List<FieldDef> fields, PhpDataType type, int refNo) {
        this.name = name;
        this.fields = fields;
        this.type = type;
        this.refNo = refNo;
    }

    public String getName() {
        return name;
    }

    public PhpDataType getType() {
        return type;
    }

    public List<FieldDef> getFields() {
        return fields;
    }

    public int getRefNo() {
        return refNo;
    }

    public boolean isFirstVisit() {
        return firstVisit;
    }

    public void setFirstVisit(boolean firstVisit) {
        this.firstVisit = firstVisit;
    }

    public void setType(PhpDataType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return  name +"."+ type +"." + refNo;
    }
}
