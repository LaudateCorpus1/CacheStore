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

import com.sm.query.QueryListenerImpl;
import com.sm.query.QueryVisitorImpl;
import com.sm.query.impl.Condition;
import org.testng.annotations.Test;

import java.security.MessageDigest;

public class TestQuery {

    @Test(groups = "{Select}")
    public void testSelect() {
        Person.Address address = new Person.Address(new Person.Street(4813, "corsica dr"), 90630, "cypress");
        Person person = new Person("mickey", 30, 4000.00, true, address);
        String queryStr = "select  name, age, male from Person where age > 1";
        QueryVisitorImpl objectQueryVisitor = new QueryVisitorImpl(queryStr);
        Object obj = objectQueryVisitor.runQuery(person);
        System.out.println(obj == null ? "null" : obj.toString());
        queryStr = "select  address.Street.no, name, age, male, address.city, address.zip from Person where age < 40 ";
        objectQueryVisitor.setQueryStr(queryStr);
        obj = objectQueryVisitor.runQuery(person);
        System.out.println(obj == null ? "null" : obj.toString());

    }

    @Test(groups = "{replace}")
    public void testReplace() {
        Person.Address address = new Person.Address(new Person.Street(4813, "corsica dr"), 90630, "cypress");
        Person person = new Person("mickey", 30, 4000.00, true, null);
        String queryStr = "replace Person set name=\"Test\", age = 20, male = false, address = { zip = 91100, city = \"irvine\" }  ";
        QueryVisitorImpl objectQueryVisitor = new QueryVisitorImpl(queryStr, person);
        Object obj = objectQueryVisitor.runQuery(person);
        System.out.println(objectQueryVisitor.getSource());
        queryStr = "replace address set zip = 92200, city = \"long beach\"  ";
        objectQueryVisitor = new QueryVisitorImpl(queryStr, person);
        obj = objectQueryVisitor.runQuery(person);
        System.out.println(objectQueryVisitor.getSource()+" "+objectQueryVisitor.getSelectObj());
    }

    @Test(groups ="{SHA-2}")
    public void testSha2() throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] diget = sha.digest("This is some text".getBytes("UTF-8"));
        System.out.println("sha2 Length "+diget.length);
        sha = MessageDigest.getInstance("MD5");
        byte[] diget2 =sha.digest("This is some text".getBytes("UTF-8"));
        System.out.println("md5 Length "+diget2.length);
    }

//    @Test(groups = "{test}")
//    public void testData(){
//        RemoteStore remoteScanStore = new RemoteStore("/Users/mhsieh/java/dev/cachestore/server/data/store", new HessianSerializer(), 0);
//        HessianSerializer serializer = new HessianSerializer();
//        String queryStr = "select  name, age, male from Person where key# in (11,12,13,14) or (key# = 10 +20 and age = 120) or (key# >= 40 and key# <= 80)";
//        queryStr = "select  name, age, male from Person where key# >= 2 and key# <= 10" ;
//        QueryListenerImpl queryListener = new QueryListenerImpl( queryStr);
//        queryListener.walkTree();
//        System.out.println(queryListener.getPredicateStack().toString() + " exit result " + queryListener.isTableScan());
//        Stack<Predicate> stack =  (Stack<Predicate>) queryListener.getPredicateStack().clone();
//        System.out.println( stack.size()+ " "+stack.toString());
//        QueryIterator queryIterator = new QueryIterator(queryListener.getPredicateStack(),queryListener.isTableScan(), remoteScanStore );
//        QueryVisitorImpl visitor = new QueryVisitorImpl("select  name, age, male from Person where key# >=2 and key# <= 10");
//        while (queryIterator.hasNext()) {
//            Pair<Key, Value> pair = queryIterator.next();
//            visitor.setKey( pair.getFirst());
//            Object result = visitor.runQuery( serializer.toObject((byte[]) pair.getSecond().getData()));
//            if ( result != null)
//                System.out.println(visitor.getKey().toString()+"  "+result.toString());
//        }
//    }

    @Test(groups = "{walk}")
    public void walkTree() {
        //queryStr = "select  name, age, male from Person where (key# in (10,14) or key# = 120) or ( key# >= 20 and key# <= 100)  or (age = 30 and key# = 80+20)";
        String queryStr = "select  name, age, male from Person where male = true and age < 10" ;
        QueryListenerImpl queryListener = new QueryListenerImpl( queryStr);
        queryListener.walkTree();
        System.out.println(queryListener.getPredicateStack().toString() + " exit result " + queryListener.isTableScan());
        queryStr = "select  name, age, male from Person where key# in (11,12,13,14) or (key# = 30 and age = 120) or (key# >= 40 and key# <= 80)";
        queryListener = new QueryListenerImpl( queryStr);
        queryListener.walkTree();
        System.out.println(queryListener.getPredicateStack().toString() + " exit result " + queryListener.isTableScan());
        if ( queryListener.getPredicateStack() != null ) {
            Condition condition = (Condition) queryListener.getPredicateStack().pop();
            condition.getOperator();
        }
    }

    @Test(groups = "{walk}")
    public void testWalk() {
        String queryStr = "select  name, age, male from Person where key# in (11,12,13,14) or (key# = 10 +20 and age = 120) or (key# >= 40 and key# <= 80)";
        QueryVisitorImpl visitor = new QueryVisitorImpl( queryStr);
        visitor.runKeyPredicate();
    }

    public static long encodeRV(long version, int record) {
        long rv = (long) record << 32;
        long v1 = 0x0000000FFFFFFFFL & version;
        return ( rv | ( 0x0000000FFFFFFFFL & version) );
    }

    public static int decodeRecord(long rv) {
        int value = (int) ((rv & 0xFFFFFFFF00000000L) >>> 32);
        return value;
    }

    public static long decodeVersion(long rv) {
        return  0x0000000FFFFFFFFL & rv  ;
    }

    @Test
    public void testDecode(){
        long v = encodeRV( 1<<16L, 1<<20 );
        System.out.println("r ="+ decodeRecord(v ));
        System.out.println("v ="+decodeVersion(v));
    }
    /*
    visit the predicate
    case operator or
    visit left
    visit right
    in get operator
    A predicate Iterator,
    The constructor pass (Store, Predicate)
    next() return KeyValue pair
     */
}
