package test;

import com.sm.query.Predicate;
import com.sm.query.PredicateEstimator;
import com.sm.query.PredicateNodes;
import com.sm.query.SchemaPredicateVisitorImpl;
import com.sm.query.parser.ObjectQueryLexer;
import com.sm.query.parser.ObjectQueryParser;
import com.sm.query.utils.Column;
import com.sm.test.DataMap;
import com.sm.transport.Utils;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mhsieh on 12/2/14.
 */
public class TestQueryObject {
    private static final Log logger = LogFactory.getLog(TestQueryObject.class);

    @Test
    public void testBits() {
        boolean b = ((byte) 0xF0 & 0x01 << 7) > 0 ;
        Assert.assertTrue(b);
        b = ((byte) 0x03 & 0x01 << 1) > 0 ;
        Assert.assertTrue(b);
    }

    @Test
    public void testSchema() {
        byte[] bs = "2a37af656f74f2fcf969134f6ae817d0".getBytes();
        Map<String, Column> map = new HashMap<String, Column>();
        SchemaPredicateVisitorImpl schema = new SchemaPredicateVisitorImpl("bitsOr(this, (10,20,30) )", map);
        boolean bl = schema.runPredicate( bs);
        System.out.println("or result "+bl);
        schema.setQueryStr("bitsAnd(this, (10,20,30) )");
        bl = schema.runPredicate( bs);
        System.out.println("and result "+bl);
    }

    @Test
    public void testParas(){
        PredicateEstimator.Source source = PredicateEstimator.Source.CRM;
        System.out.println("source " + source.name());
        DataMap map = new DataMap();
        map.populateMap();
        PredicateEstimator predicateEstimator = new PredicateEstimator(map);
        //double rate = predicateEstimator.runEstimate(" foods = 1 or other = 2 ");
        //double rate = predicateEstimator.runEstimate(" (((uk.gender = \"male\" or age = \"25-34\")))");
        double rate = predicateEstimator.runEstimate("( (us.age = \"18-24\") and (exp = \"1.1\") ) ");
        DataMap dataMap = new DataMap();
        dataMap.populateMap();
        PredicateEstimator estimator = new PredicateEstimator(dataMap);

        System.out.println( findParas("a", "b", "c"));
    }

    public static int findParas(String... var) {
        int i = 0;
        for ( String each : var) {
            System.out.println( each);
            i++;
        }
        return i;
    }

    @Test
    public void testGenPredicate() {
        PredicateNodes predicateNode = new PredicateNodes();
        Predicate predicate = predicateNode.generatePredicate("( ms = \"1.1\" and uk.age = \"18-24\" ) or  not ( age =1 and ms = 12 ) ");
        System.out.println( predicate.toString());
        System.out.println( predicateNode.generatePredicate("ms = \"1.1\" "));

    }

    public static void main(String[] args) throws Exception  {
        String[] opts = new String[] {"-query"};
        String[] defaults = new String[] {"replace Order set name = \"test\", campaign.id = 10 , booking.no =20.2 where not record# = 1  ; insert Campaign set cid=20, cname=\"go\""};
        String[] paras = Utils.getOpts(args, opts, defaults);
        //String query = paras[0] ;
        String query = "select id, name, bookings from order where id = 10";
        //CharStream input = new UnbufferedTokenStream(new ANTLRInputStream( new StringReader(query)));
        ObjectQueryLexer lexer = new ObjectQueryLexer( new ANTLRInputStream( new StringReader(query)));
        CommonTokenStream token = new CommonTokenStream( lexer);
        ObjectQueryParser objectQueryParser = new ObjectQueryParser( token);
        objectQueryParser.setBuildParseTree( true);
        ParserRuleContext tree = objectQueryParser.script();
        ParseTreeWalker walker  = new ParseTreeWalker();
        System.out.println(walker.getClass().getName()+ " simple "+ walker.getClass().getSimpleName());
        System.out.println( tree.toString()) ;
    }
}
