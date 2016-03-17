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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sm.query.ObjectQueryVisitorImpl;
import com.sm.query.PredicateEstimator;
import com.sm.query.PredicateVisitorImpl;
import com.sm.query.Result;
import com.sm.query.utils.ClassInfo;
import com.sm.test.DataMap;
import com.sm.transport.Utils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

import static com.sm.query.utils.QueryUtils.createInstance;
import static com.sm.query.utils.QueryUtils.findClassInfo;
import static test.Person.Address;
import static test.Person.Street;

public class TestPredImpl {

    @Test(groups ="{Select}")
    public void testSelect(){
        Address address = new Address( new Street(4813, "corsica dr"), 90630, "cypress");
        Person person = new Person("mickey", 30, 4000.00, true, address);
        String queryStr = "select  name, age, male from Person where age > 10";
        ObjectQueryVisitorImpl objectQueryVisitor = new ObjectQueryVisitorImpl( queryStr);
        Object obj =objectQueryVisitor.runQuery(person);
        System.out.println( obj.toString());
        queryStr = "select  address.Street.no, name, age, male, address.city, address.zip from Person where age > 10";
        objectQueryVisitor.setQueryStr( queryStr);
        obj =objectQueryVisitor.runQuery(person);
        System.out.println( obj.toString());

    }

    @Test(groups = "{replace} ")
    public void testReplace() throws JsonProcessingException {
        Address address = new Address( new Street(4813, "corsica dr"), 90630, "cypress");
        Person person = new Person("mickey", 30, 4000.00, true, null);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("Mapper "+ mapper.writeValueAsString( person)) ;
        String queryStr = "replace Person set name=\"Test\", age = 10, male = false, address = { zip = 90630, city = \"cypress\" } ; ";
        ObjectQueryVisitorImpl objectQueryVisitor = new ObjectQueryVisitorImpl( queryStr, person);
        Object obj =objectQueryVisitor.runQuery(person);
        System.out.println( obj.toString());
        System.out.println(objectQueryVisitor.getSource().toString());
    }

    @Test(groups = "{query}" )
    public void testQuery(){
        Person person = new Person("mickey", 30, 4000.00, true, null);
        List<String> lst = new ArrayList<String>();
        lst.add("t1"); lst.add("t2");
        person.setList(lst);
        Set<String> set = new HashSet<String>();
        set.addAll( lst);

        List<String> lst1 = new ArrayList<String>();
        lst1.add("t3"); lst1.add("t4");
        Set<String> set1 = new HashSet<String>();
        set1.addAll(lst1);
        List<List<String>> nest = new ArrayList<List<String>>();
        nest.add(lst); nest.add(lst1);
        person.setNestLists( nest);
        Set<Set<String>> setAddr = new HashSet<Set<String>>();
        setAddr.add(set); setAddr.add(set1);
        person.setSetAdd(setAddr);
        List<Address> lstAd = new ArrayList<Address>();
        Address add1 = new Address() ;
        add1.setLst( lst);
        Address add2 = new Address();
        add2.setLst( lst1);
        lstAd.add(add1); lstAd.add(add2);
        person.setAddressList(lstAd);

        String queryStr1 = " existListOr(addressList, lst, ( \"t1\", \"t2\" ) ) ";;
        PredicateVisitorImpl predicateVisitor1 = new PredicateVisitorImpl(queryStr1, person);
        boolean r1 = predicateVisitor1.runPredicate(person);
        System.out.println( "r1 ="+r1);
        Assert.assertEquals( r1, true);
        String queryStr2 = " exist(addressList, lst, \"t5\" )";
        PredicateVisitorImpl predicateVisitor2 = new PredicateVisitorImpl(queryStr2, person);
        boolean r2 = predicateVisitor2.runPredicate(person);
        System.out.println( "r2 ="+r2);
        Assert.assertEquals( r2, false);
        queryStr1 = " existListOr(list, ( \"t1\", \"t2\" ) ) ";
        predicateVisitor2 = new PredicateVisitorImpl(queryStr1, person);
        r2 = predicateVisitor2.runPredicate(person);
        System.out.println( "t1, t2 r2 ="+r2);
        //Person p1 = person.getClass().newInstance();
        String queryStr = " exist(list, \"t3\" )";
        //queryStr = " age in (30, 40,50) ";
        PredicateVisitorImpl predicateVisitor = new PredicateVisitorImpl(queryStr, person);
        boolean result = predicateVisitor.runPredicate(person);
        System.out.println("result "+result);
        Assert.assertEquals(result, false);
        queryStr = " substr(name,0) = \"mickey\" ";
        predicateVisitor.setQueryStr( queryStr);
        result = predicateVisitor.runPredicate(person);
        System.out.println("result " + result);
        Assert.assertEquals( result , true);
        List<Address> addressList = new ArrayList<Address>();
        addressList.add( new Address(null, 90610, "cypress"));
        addressList.add( new Address(null, 90620, "cypress"));
        person.setAddressList( addressList);
        queryStr = " exist(addressList, zip, 90610) ";
        predicateVisitor.setQueryStr( queryStr);
        result = predicateVisitor.runPredicate(person);
        System.out.println("result " + result);
        Assert.assertEquals(result, true);
        queryStr = " count(addressList, zip, 90610) > 1 ";
        predicateVisitor.setQueryStr( queryStr);
        result = predicateVisitor.runPredicate(person);
        System.out.println("result " + result);
        Assert.assertEquals( result, false);
        queryStr = " existListOr(list, ( \"t1\", \"t2\" ) ) ";
        predicateVisitor = new PredicateVisitorImpl(queryStr);
        result = predicateVisitor.runPredicate(person);
        System.out.println("result, list t1,t2 " + result+ person.getList());
        Assert.assertEquals( result, true);
    }

    @Test(groups = "{predicate}" )
    public void testOne() {
        PredicateEstimator.Source source = PredicateEstimator.Source.CRM;
        System.out.println("source " + source.name());
        DataMap map = new DataMap();
        map.populateMap();
        PredicateEstimator predicateEstimator = new PredicateEstimator(map);
        double rate = predicateEstimator.runEstimate(" foods = 1 or other = 2 ");
        //double rate = predicateEstimator.runEstimate(" (((gender = \"male\" or foods = \"1.1\")))");
        //double rate = predicateEstimator.runEstimate("( foods = 1 and other = 2) and gender = \"male\" and not age = \"25-34\" ");
        // foods.1.1
        //String sce = predicateEstimator.getSourceStack().pop().getValue() ;
        double ncs = predicateEstimator.getPopulation();
        double value = Math.abs(Math.round(ncs*rate));
        System.out.println("result of dr "+ rate +" total "+ value+ " ncs "+ncs );
        //predicateEstimator
        Address address = new Address( new Street(4813, "corsica dr"), 90630, "cypress");
        Set<Short> setShort = new HashSet<Short>();
        setShort.add( (short) 1); setShort.add( (short) 2);setShort.add( (short) 3);
        Person person = new Person("mickey", 30, 4000.00, true, address);
        person.setSetShort(setShort);
        String q = "exist(setShort, 1) ";
        PredicateVisitorImpl impl2 = new PredicateVisitorImpl(q);
        boolean r2 = impl2.runPredicate(person);
        System.out.println("result of r2 "+r2);
        Assert.assertEquals( r2, true);
        HashSet<String> set = new HashSet<String>();
        set.add("t1"); set.add("t2"); set.add("t3");
        address.setSet( set);
        String query1 = "exist(set, \"t1\") ";
        PredicateVisitorImpl impl1 = new PredicateVisitorImpl(query1);
        boolean r1 = impl1.runPredicate(address);
        System.out.println("result of r "+r1);
        Assert.assertEquals( r2, true);
        String query = " age > 10 and income < 40.00 and male = true ";
        PredicateVisitorImpl impl = new PredicateVisitorImpl(query);
        boolean r = impl.runPredicate(person);
        System.out.println("result of r "+r);
    }

    class Test1 {
        private int a;
        private long b = -1;
        private String c ;
    }


    @Test(groups = "{instance}")
    public void testInstance() throws Exception {
        System.out.println("mickey".substring(2));
        Person person = new Person("mickey", 30, 4000.00, true, null);
        List<String> lst = new ArrayList<String>();
        lst.add("t1"); lst.add("t2");
        person.setList( lst);
        Object obj= createInstance( person.getClass());
        System.out.println(obj.toString());
    }

    public static void main(String[] args) throws Exception {
        String[] opts = new String[]{"-query"};
        String[] defaults = new String[]{"field is null "};
        String[] paras = Utils.getOpts(args, opts, defaults);
        List<String> list = new ArrayList<String>();
        list.add("test1");
        Map<String, ClassInfo> map = new HashMap<String, ClassInfo>();
        ClassInfo classInfo = findClassInfo( list, map);
        //System.out.println( classInfo.toString());
        Result r1 = new Result(10);
        Result r2 = new Result(20L);
        Result r3 = new Result( new Integer(30));
        int i1 = (Integer) r1.getValue();


    }
}
