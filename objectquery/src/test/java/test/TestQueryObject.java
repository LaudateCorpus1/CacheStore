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

import com.sm.query.parser.ObjectQueryLexer;
import com.sm.query.parser.ObjectQueryParser;
import com.sm.transport.Utils;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.StringReader;

public class TestQueryObject {
    private static final Log logger = LogFactory.getLog(TestQueryObject.class);

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
