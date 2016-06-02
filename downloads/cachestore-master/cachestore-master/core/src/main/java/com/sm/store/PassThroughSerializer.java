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

package com.sm.store;

import com.sm.storage.Serializer;

/**
 * Created by mhsieh on 4/25/16.
 * pass through as name imply that does nothing
 * client side that did nothing, grpc broker server is the one use case
 * that did pass through value.getData()
 */
public class PassThroughSerializer implements Serializer {


    @Override
    public byte[] toBytes(Object object) {
        return (byte[]) object;
    }

    @Override
    public Object toObject(byte[] bytes) {
        return bytes;
    }
}
