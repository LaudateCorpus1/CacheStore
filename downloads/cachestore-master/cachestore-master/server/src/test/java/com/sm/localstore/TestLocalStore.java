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

package com.sm.localstore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

import static org.testng.Assert.assertEquals;

public class TestLocalStore {
    private static Log logger = LogFactory.getLog(TestLocalStore.class);

    public static String[] checkPath(String filename){
        String path = "./";
        String name ;
        int sep = filename.lastIndexOf( File.separator );
        if ( sep >= 0 ) {
            path = filename.substring(0, sep+1);
            name = filename.substring( sep+1, filename.length() );

        }
        else {
            int slash = filename.lastIndexOf("/");
            // no file seperator
            if (slash >= 0 ) {
                path = filename.substring(0, slash+1);
                name = filename.substring( slash+1, filename.length() );
            }
            else name = filename ;
        }
        //String[] toReturn = new String[]
        return new String[] { path, name};
    }

    public static void output(String[] strs){
        for ( String str : strs)
            System.out.print(str+" , ");
        System.out.print("\n");
    }

    //@Test(groups = {"block"} )
    public static void testpath() {
        String p1 = "/u1/test/test";
        //output( parsePath( p1));
        String[] toReturn = checkPath( p1);
        assertEquals(toReturn[0], "/u1/test/");
        assertEquals(toReturn[1], "test");
        String p2 ="test";
        toReturn = checkPath( p2);
        assertEquals( toReturn[0], "./");
        assertEquals( toReturn[1], "test");
        //output( parsePath(p2));
        String p3 ="/test/test/";
        toReturn = checkPath( p3);
        //output( parsePath(p3));
        assertEquals( toReturn[0], "/test/test/");
        assertEquals( toReturn[1], "");


    }

//    public static void main(String[] args){
//        String p1 = "/u1/test/test";
//        output( parsePath( p1));
//        String p2 ="test";
//        output( parsePath(p2));
//        String p3 ="/test/test/";
//        output( parsePath(p3));
//
//    }
}
