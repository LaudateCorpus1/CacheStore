package test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sm.query.*;
import com.sm.query.utils.ClassInfo;
import com.sm.query.utils.Column;
import com.sm.test.DataMap;
import com.sm.transport.Utils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

import static com.sm.query.utils.QueryUtils.createInstance;
import static com.sm.query.utils.QueryUtils.findClassInfo;
import static test.Person.Address;
import static test.Person.Street;

/**
 * Created by mhsieh on 12/30/14.
 */
public class TestPredImpl {

    public static Map<String, Column> buildMap() {
        Map<String, Column> map = new HashMap<String, Column>();
        map.put("carMake", new Column("carMake", Result.Type.BYTE, 0));
        map.put("carClass", new Column("carClass", Result.Type.BYTE, 1));
        map.put("carAge", new Column("carAge", Result.Type.BYTE, 2));
        map.put("carInsurance", new Column("carInsurance", Result.Type.BYTE, 3));
        map.put("demoHomeOwner", new Column("demoHomeOwner", Result.Type.BYTE, 4));
        return map;
    }

    @Test(enabled = false)
    public void orderOfOperations() {
        TestTrue testTrue = new TestTrue();
        String queryStr = "t = true or t = true and t = false";
        PredicateVisitorImpl visitor = new PredicateVisitorImpl(queryStr);
        boolean b = visitor.runPredicate(testTrue);
        //Assert.assertEquals(b, true || true && false);
    }

    public static class TestTrue {
        boolean t = true;
    }

    @Test
    public void testSchema(){
        Test1 test1 = new Test1();
        test1.bytes = "test".getBytes();
        test1.c = "test";
        String queryStr = "select  * from Test1 where c != null and c = \"test\" ";
        QueryVisitorImpl objectQueryVisitor = new QueryVisitorImpl(queryStr);
        Object obj = objectQueryVisitor.runQuery(test1);
        System.out.println( obj ==null ? "null" : obj.toString());
        String queryStr1 = "select  * from Test1 where key# != strToBytes(\"test\") and country !=  lower(country) ";
        QueryListenerImpl queryListener = new QueryListenerImpl( queryStr1);
        queryListener.walkTree();
        System.out.println(queryListener.getPredicateStack().toString() + " exit result " + queryListener.isTableScan());

//        queryListener = new QueryListenerImpl( queryStr);
//        queryListener.walkTree();
//        System.out.println(queryListener.getPredicateStack().toString() + " exit result " + queryListener.isTableScan());
//        QueryVisitorImpl objectQueryVisitor = new QueryVisitorImpl(queryStr);
//        Object obj = objectQueryVisitor.runQuery(test1);
//        System.out.println("test "+obj.toString());
//        byte[] bytes = new byte[]{ 0x04,0x05,0x06,0x07,0x08 };
//        Filter filter = new SchemaPredicateVisitorImpl("carMake =4 and carClass = 5", buildMap());
//        boolean bl =filter.runPredicate(bytes);
//        Assert.assertTrue( bl);
//        filter = new SchemaPredicateVisitorImpl("carMake =5 and carClass = 5", buildMap());
//        bl =filter.runPredicate(bytes);
//        Assert.assertFalse(bl);
//        //System.out.println("bl "+bl);
//        filter = new SchemaPredicateVisitorImpl("carMake =6 or carClass = 10", buildMap());
//        bl =filter.runPredicate(bytes);
//        //System.out.println("bl "+bl);
//        Assert.assertFalse(bl);

    }


    @Test(groups ="{Select}")
    public void testSelect(){
        Person.Address address = new Person.Address(new Person.Street(4813, "corsica dr"), 90630, "cypress");
        Person person = new Person("mickey", 30, 4000.00, true, address);
        String queryStr = "select  name, age, male from Person where not age > 40";
        QueryVisitorImpl objectQueryVisitor = new QueryVisitorImpl(queryStr);
        Object obj = objectQueryVisitor.runQuery(person);
        System.out.println(obj == null ? "null" : obj.toString());
        queryStr = "select  address.Street.no, name, age, male, address.city, address.zip from Person where age < 40 ";
        objectQueryVisitor.setQueryStr(queryStr);
        obj = objectQueryVisitor.runQuery(person);
        System.out.println(obj == null ? "null" : obj.toString());
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
        PredicateVisitorImpl predicateVisitor1 = new PredicateVisitorImpl(queryStr1);
        boolean r1 = predicateVisitor1.runPredicate(person);
        System.out.println( "r1 ="+r1);
        Assert.assertEquals( r1, true);
        String queryStr2 = " exist(addressList, lst, \"t5\" )";
        PredicateVisitorImpl predicateVisitor2 = new PredicateVisitorImpl(queryStr2);
        boolean r2 = predicateVisitor2.runPredicate(person);
        System.out.println( "r2 ="+r2);
        Assert.assertEquals( r2, false);
        queryStr1 = " existListOr(list, ( \"t1\", \"t2\" ) ) ";
        predicateVisitor2 = new PredicateVisitorImpl(queryStr1);
        r2 = predicateVisitor2.runPredicate(person);
        System.out.println( "t1, t2 r2 ="+r2);
        //Person p1 = person.getClass().newInstance();
        String queryStr = " exist(list, \"t3\" )";
        //queryStr = " age ek (30, 40,50) ";
        PredicateVisitorImpl predicateVisitor = new PredicateVisitorImpl(queryStr);
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

    @Test
    public void testMultiPredicate() {
        Person person = new Person("mickey", 30, 4000.00, true, null);
        Te te = new Te("test", 40, true);
        String queryStr1 = "Person.age > 10 and Te.ek > 5";
        queryStr1 = "Person.age > 10 and Te.ek > 30";
        PredicateVisitorImpl predicateVisitor1 = new PredicateVisitorImpl(queryStr1);
        boolean bl = predicateVisitor1.runPredicate(person, null);
        System.out.println( bl);
    }

    @Test void testMultiAlias() {
        String queryStr1 = "Person.age > 10 and Te.ek > 5";
        queryStr1 = "Person.age > 10 and Te.ek > 30 or Person.income < 300.00 and Te.bl = true";
        PredicateAlias predicateVisitor1 = new PredicateAlias(queryStr1);
        System.out.println(predicateVisitor1.findAlias(Filter.Impl.Serializer).toString());
        queryStr1 = "A.age > 10 and B.ek > 30";
        predicateVisitor1 = new PredicateAlias(queryStr1);
        System.out.println(predicateVisitor1.findAlias(Filter.Impl.Schema).toString());
    }

    @Test(groups = "{predicate}" )
    public void testOne() {
        PredicateEstimator.Source source = PredicateEstimator.Source.CRM;
        System.out.println("source " + source.name());
        DataMap map = new DataMap();
        map.populateMap();
        PredicateEstimator predicateEstimator = new PredicateEstimator(map);
        //double rate = predicateEstimator.runEstimate(" foods = 1 or other = 2 ");
        //double rate = predicateEstimator.runEstimate(" (((uk.gender = \"male\" or age = \"25-34\")))");
        double rate = predicateEstimator.runEstimate("( (us.age = \"18-24\") and (exp = \"1.1\") ) ");
        // foods.1.1
        //String sce = predicateEstimator.getSourceStack().peek().getValue() ;
        double ncs = predicateEstimator.getPopulation("uk");
        double value = Math.abs(Math.round(ncs*rate));
        System.out.println("result of dr "+ rate +" total "+ value+ " ncs "+ncs );
        //predicateEstimator
        rate = predicateEstimator.runEstimate("( (ms = \"1.1\") and (uk.age = \"18-24\") ) ");
        //ncs  = predicateEstimator.getPopulation(sce);
        ncs = predicateEstimator.getPopulation("ms");
        value = Math.abs(Math.round(ncs*rate));
        System.out.println("result of dr "+ rate +" total "+ value+ " ncs "+ncs );
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
        private byte[] bytes;

        @Override
        public String toString() {
            return "Test1{" +
                    "a=" + a +
                    ", b=" + b +
                    ", bytes=" + Arrays.toString(bytes) +
                    '}';
        }
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
