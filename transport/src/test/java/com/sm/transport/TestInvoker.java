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
 */package com.sm.transport;

import com.sm.message.Invoker;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class TestInvoker {

    @Test(groups = {"invoker"})
    public void testInvoker () {
        Invoker iv = new Invoker("test","m1", null);
        iv.addParas( new String[]{ "paras1", "paras2"});
        assertEquals( iv.getParams().length, 1);
        System.out.println(((String[])iv.getParams()[0])[0]);

        iv = new Invoker("test","m1", new Object[] {});
        iv.addParas( new String[]{ "paras1", "paras2"});
        assertEquals( iv.getParams().length, 1);

        iv = new Invoker("test","m1", new Object[] {"String"});
        iv.addParas( new String[]{ "paras1", "paras2"});
        assertEquals( iv.getParams().length, 2);
        System.out.println(((String[])iv.getParams()[1])[0]);

    }


}
