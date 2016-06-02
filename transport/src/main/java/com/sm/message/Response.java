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
 */package com.sm.message;

import java.io.Serializable;

public final class Response<T> implements Serializable {

    private T payload;
    private boolean error;


    public Response(T payload, boolean error) {
        this.payload = payload;
        this.error = error;
    }

    public Response(T payload) {
        this(payload, false);
    }


    public boolean isError() {
        return error;
    }

    public void setError(boolean error){
        this.error = error ;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "isError "+error + ( payload != null ? " payload "+payload.toString() : "");
    }

//    public void writeExternal(Hessian2Output out) throws IOException {
//        out.writeBoolean( error);
//        out.writeObject( payload);
//    }
//
//    public void readExternal(Hessian2Input in) throws IOException {
//        this.error = in.readBoolean();
//        this.payload = (T) in.readObject();
//    }
}
