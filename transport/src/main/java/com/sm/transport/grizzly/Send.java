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

package com.sm.transport.grizzly;

import com.sm.message.Request;
import com.sm.message.Response;
import org.glassfish.grizzly.Connection;

public interface Send  {
    //synchronize communication request and response
    public Response sendRequest(Request request, Connection connection);
    // asynchronized model - send and gone
    public void sendMessage(Request message, Connection connection);
    public Response invoke(Request request, Connection connection);
}
