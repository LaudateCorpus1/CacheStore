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

package com.sm.query;

import com.sm.query.parser.PredicateBaseListener;
import com.sm.query.parser.PredicateLexer;
import com.sm.query.parser.PredicateParser;
import com.sm.query.utils.QueryException;
import com.sm.query.utils.QueryUtils;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.StringReader;
import java.util.Stack;

public class PredicateEstimator extends PredicateBaseListener {

    private static final Log logger = LogFactory.getLog(PredicateEstimator.class);

    private EstimateMap dataMap;
    private Stack<Result> valueStack = new Stack<Result>();
    private Stack<Double> estimateStack = new Stack<Double>();
    private String queryStr;
    private Stack<String> idStack = new Stack<String>();
    private Stack<Source> sourceStack = new Stack<Source>();
    public static double IGNORE = -1.00;

    /**
     * dataMap consist of String Key  (Attribute.Value) as Key, Value is Double to represent as %
     * examples "food.frozenPizza.3" as key, Value is 0.08 represents 8%
     * @param dataMap
     */
    public PredicateEstimator(EstimateMap dataMap) {
        this.dataMap = dataMap;
    }


    public void walkTree() {
        try {
            init();
            PredicateLexer lexer = new PredicateLexer(new ANTLRInputStream(new StringReader(queryStr)));
            CommonTokenStream token = new CommonTokenStream(lexer);
            PredicateParser parser = new PredicateParser(token);
            parser.setBuildParseTree(true);
            PredicateParser.ScriptContext tree = parser.script(); // parse
            ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
            parseTreeWalker.walk( this, tree);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new QueryException( ex.getMessage(), ex );
        }
    }

    private void init() {
        if ( ! valueStack.isEmpty())
            valueStack.empty();
        if ( ! idStack.isEmpty())
            idStack = new Stack<String>();
        if ( ! estimateStack.isEmpty()) {
            estimateStack.empty();
        }
        if ( ! sourceStack.isEmpty()) {
            sourceStack.empty();
        }
    }

    /**
     *
     * @param queryStr - predicate string
     * @return rate of estimation
     */
    public double runEstimate(String queryStr) {
        if ( queryStr == null || queryStr.length() == 0) {
            logger.info("queryStr is null or length = 0, return 1 as 100%") ;
            return 1.00;
        }
        else {
            this.queryStr = queryStr;
            logger.info("query "+queryStr) ;
            walkTree();
            //go through queryStr to find estimate
            if ( estimateStack.isEmpty())
                return 1.00;
            else
                return estimateStack.peek();
        }
    }

    public EstimateMap getDataMap() {
        return dataMap;
    }

    public Stack<Result> getValueStack() {
        return valueStack;
    }

    public Stack<Source> getSourceStack() {
        return sourceStack;
    }

    public Double getPopulation() {
        if ( sourceStack.empty()) {
            if ( ! idStack.isEmpty()) {
                Source source = findSource( idStack.peek() );
                logger.info("idStack is not empty, using source as "+source.value);
                return dataMap.get( source.value);
            }
            else {
                logger.info("source map is empty, queryStr " + queryStr);
                return dataMap.get(Source.CRM.value);
            }
        } else {
            Source sce = sourceStack.peek();
            return dataMap.get(sce.getValue());
        }
    }

    @Override
    public void exitExpValue(PredicateParser.ExpValueContext ctx) {
        Result result;
        if ( ctx.value() instanceof PredicateParser.NumbersContext)
            result = new Result(Result.Type.NUMBER, ctx.getText());
        else if ( ctx.value() instanceof PredicateParser.StringsContext)
            result = new Result( Result.Type.STRING, ctx.getText().substring(1, ctx.getText().length() - 1));
        else if ( ctx.value() instanceof PredicateParser.BooleansContext)
            result = new Result(Result.Type.BOOLEAN, Boolean.valueOf(ctx.getText()));
        else if ( ctx.value() instanceof PredicateParser.NullsContext)
            result = new Result(null);
        else
            result = null;
        if ( result != null )
            valueStack.push(result);
        else
            throw new QueryException("result is not Number or String v= "+ctx.getText());
    }

    @Override
    public void exitExpr(@NotNull PredicateParser.ExprContext ctx) {
        //System.out.println(ctx.binaryOperator().getText());
        Result right = valueStack.pop();
        Result left  = valueStack.pop();
        Result temp =  QueryUtils.binaryOp(left, ctx.binaryOperator().getText(), right);
        valueStack.push( temp);
    }

    @Override
    public void exitComparison(PredicateParser.ComparisonContext ctx) {
        String objectId = ctx.objectField().getText();
        idStack.push( objectId);
        String op = ctx.comparisonOperator().getText();
        Result result = valueStack.pop();
        double r = findDistributionValue( objectId+"."+result.getValue().toString());
        if ( r == IGNORE ) {
            if ( op.equals("!="))
                estimateStack.push(1.00);
            else
                estimateStack.push(0.00);
        }
        else {
            if ( op.equals("!="))
                estimateStack.push( 1- r);
            else
                estimateStack.push(r);
        }
        System.out.println(objectId+" "+op+" "+result.getValue().toString());

    }

    @Override
    public void exitInComp(PredicateParser.InCompContext ctx) {
        String objectId = ctx.objectField().getText();
        idStack.push( objectId);
        String op = "in";
        Result result ;
        StringBuffer sb = new StringBuffer();
        double total = 0.00 ;
        for( PredicateParser.ExpressionContext each : ctx.expression() ) {
            result = valueStack.pop();
            double r = findDistributionValue( objectId+"."+result.getValue().toString());
            if ( r != IGNORE)
                total =+ r;
            sb.append(result.toString()).append(",");
        }
        estimateStack.push( total);
        System.out.println(objectId+" "+op+" "+sb.toString());
    }

    @Override
    public void exitExistExpr(PredicateParser.ExistExprContext ctx) {
        String objectId;
        if ( ctx.objectField().size() == 1 ) {
            objectId = ctx.objectField(0).getText();
        }
        else {
            objectId = ctx.objectField(1).getText();
        }
        Result result = valueStack.pop();
        StringBuffer sb = new StringBuffer();
        double total = 0.00 ;
        estimateStack.push( total);
    }

    private double findDistributionValue(String key) {
        //idStack.push( key);
        // find  key distribution from dataMap
        if ( dataMap.get(key ) == null) {
            logger.error(key + " is not in dataMap");
            return IGNORE;
        }
        else {
            return dataMap.get(key);
        }
    }


    @Override public void enterExistListOr(PredicateParser.ExistListOrContext ctx) {
    }

    @Override
    public void exitNotPredicate(PredicateParser.NotPredicateContext ctx) {
            // not predicate, reverse by subtracted by 1
        double result = 1 - (Double) estimateStack.pop() ;
        estimateStack.push(result);
    }

    private double checkValue(double value) {
        if ( value < 0.00 ) {
            logger.error( "value < zero "+ value);
            value = Math.abs(value);
        }
        if ( value > 1.00) {
            logger.error( "value > 1 "+ value);
            return 1.00;
        }
        else
            return value;

    }

    public final static String NCS ="foods,beverages,other,";
    private double findInterSec(String match, double left, double right) {
       if ( match.length() == 0)
           return left * right;
       else {
           //check if NCS field, always return left * right
           if ( NCS.indexOf(match+",") >=0  )
               return left * right;
           else
               return 0;
       }
    }

    public static enum Source {
        NCS("ncs"), CRM("crm") ;

        final String value;
        Source(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

    protected Source findSource(String objectId) {
        if (objectId.indexOf(",") >= 0 ) { //multiple fields
            return sourceStack.pop();
        }
        else {  //it is a simple field
            if (NCS.indexOf(objectId + ",") >= 0)
                return Source.NCS;
            else
                return Source.CRM;
        }
    }

    protected Source determineSource(Source left, Source right, String operator){
        if ( left == right ) {
            return left;
        } else {
            if ( operator.equals("and")) {
                return findAnd( left, right);
            } else { // or
                return findOr(left, right);
            }
        }
    }

    protected Source findAnd(Source left, Source right) {
        if ( left == Source.NCS ) return left;
        else if ( right == Source.NCS ) return right;
        else {
            return Source.CRM;
        }
    }

    protected Source findOr(Source left, Source right) {
        if ( left == Source.CRM ) return left;
        else if ( right == Source.CRM ) return right;
        else {
            return Source.NCS;
        }
    }

    private String findSameId(String left, String right) {
        String[] ls = left.split(",");
        String[] rs = right.split(",");
        for ( String each : ls) {
            for ( String eh : rs) {
                if ( each.equals( eh))
                    return each;
            }
        }
        //when there is no match, return empty string
        return "";
    }

    private String merge(String left, String right, String match) {
        String[] ls = left.split(",");
        String[] rs = right.split(",");
        StringBuffer sb = new StringBuffer();
        boolean find = false;
        for (String each : ls) {
            if ( each.equals( match)) {
                find = true;
            }
            else
                sb.append(each +",");
        }
        for (String each : rs) {
            if ( !find ) {
                if (each.equals(match)) {
                    find = true;
                } else
                    sb.append(each + ",");
            }
            else {
                sb.append(each +",");
            }
        }
        return sb.toString();
    }

    private double getRatio(Source source, Source target, String op){
        if ( source == Source.CRM && target == Source.NCS &&  op.equals("or"))
            return NCS_CRM;
        else if ( source == Source.NCS && target == Source.CRM && op.equals("and"))
            return 1.00;
        else {
            logger.warn("uncover case "+source+" target "+target);
            return 1.00;
        }

    }

    public static final double NCS_CRM = 77.0 /1200 ;

    @Override
    public void exitNormal(PredicateParser.NormalContext ctx) {
        String rightId = idStack.pop();
        String leftId = idStack.pop();
        String match = findSameId( rightId, leftId);
        if ( match.length() == 0 )
            idStack.push(rightId+","+leftId);
        else
            idStack.push( merge( rightId, leftId, match) );


        double right = estimateStack.pop();
        double left  = estimateStack.pop();

        Source rs = findSource( rightId);
        Source ls = findSource( leftId);
        Source source ;
        String op =  ctx.logicalOperator().getText();
        if ( ls != rs) {
            source = determineSource(ls, rs, op);
            if ( source != ls )
                left = left * getRatio(source, ls, op);
            if ( source != rs)
                right = right * getRatio(source, rs, op);
        }
        else { // no adjustment needed
            source = ls ;
        }
        sourceStack.push( source);
        double interSec = findInterSec( match, left, right );
        if ( op.equals("or")) {
            System.out.println("Or left "+left+ " right "+right );
            double value = (left+right) - interSec ;
            estimateStack.push( checkValue(value) );
        }
        else {  //and
            System.out.println("And left "+left+ " right "+right );
            estimateStack.push( checkValue(interSec) );
        }
    }

}
