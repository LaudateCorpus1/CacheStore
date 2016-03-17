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

package test;

import com.sm.store.loader.ExtendedGroovyLoader;
import groovy.lang.GroovyObject;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;

public class TestGroovyScript {

    @Test(groups = {"groovy"})
    public void testScript() throws Exception {
        String f1 = new File("./src/test/resources/StoreProc.groovy").getCanonicalPath();
        URL url = new URL("file://"+f1);
        ExtendedGroovyLoader classLoader = new ExtendedGroovyLoader();
        Class cls = classLoader.parseClass(new File(f1));
        GroovyObject groovyObj = (GroovyObject) cls.newInstance();
        System.out.println((String) groovyObj.invokeMethod("sayHello", new Object[] { "test" }) );
        groovyObj.getMetaClass().setProperty(groovyObj, "city", "cypress");
        System.out.println( groovyObj.getMetaClass().getProperty(groovyObj,"city"));
        Method[] methods = cls.getDeclaredMethods();
        System.out.println("methods "+ methods.length);
        dumpMethod( methods);
        if ( classLoader.isSourceChange(url, cls)) {
            System.out.println("source change for f1 "+f1);
            Class cls1 = classLoader.recompile(url, groovyObj.getClass().getName(), cls);
            GroovyObject groovyObj1 = (GroovyObject) cls1.newInstance();
            System.out.println( groovyObj1.invokeMethod("sayHello", new Object[]{"test"}) );
            //assert "hello test".equals( output);

        }

    }

    private void dumpUrl(URL url) throws Exception {
        System.out.println("uri "+url.toURI());
        BufferedReader in = new BufferedReader(
                new InputStreamReader(url.openStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null)
            System.out.println(inputLine);
        in.close();
    }

    private void dumpMethod(Method[] methods) {
        for ( Method each : methods){
            System.out.println( each.getName());
        }

    }
}
