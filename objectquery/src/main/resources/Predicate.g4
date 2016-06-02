grammar Predicate;

script
    : (predicate)?  EOF;



predicate
	: predicate logicalOperator predicate  #Normal
	| LPAREN predicate RPAREN              #ParenPredicate
	| 'not' predicate                      #NotPredicate
	| objectPredicate                      #ObjPredicate
	| functionalPredicate                  #FunctionPredicate
    ;

objectPredicate
    : objectField comparisonOperator expression   #Comparison
    | objectField 'is' 'null'                     #CheckNull
    | objectField 'in' LPAREN expression (COMMA expression)* RPAREN  #InComp
    ;

functionalPredicate
    : 'count' LPAREN objectField ',' (objectField ',')? expression RPAREN comparisonOperator expression  #CountExpr
    | 'exist' LPAREN objectField ',' (objectField ',')? expression RPAREN                        #ExistExpr
    | 'lower' LPAREN objectField RPAREN comparisonOperator expression                            #LowerExpr
    | 'upper' LPAREN objectField RPAREN comparisonOperator expression                            #UpperExpr
    | 'substr' LPAREN objectField ',' expression RPAREN comparisonOperator expression            #SubstrExpr
    | 'existListOr' LPAREN  objectField ',' (objectField ',')? LPAREN expression (COMMA expression)* RPAREN RPAREN #ExistListOr
    | 'existListAnd' LPAREN  objectField ',' (objectField ',')? LPAREN expression (COMMA expression)* RPAREN RPAREN #ExistListAnd
    | 'bitsOr' LPAREN objectField ',' LPAREN expression (COMMA expression)* RPAREN RPAREN #BitsOr
    | 'bitsAnd' LPAREN objectField ',' LPAREN expression (COMMA expression)* RPAREN RPAREN #BitsAnd
    ;

expression
    : expression binaryOperator expression   #Expr
    | LPAREN expression RPAREN               #ExpParen
    | value                                  #ExpValue
    ;

value
    :  STRING                      #Strings
    |  NUMBER                      #Numbers
    |  BOOLEAN                     #Booleans
    |  'null'                      #Nulls
    ;

objectField
    : ID('.'ID)*
    | ID
    ;


binaryOperator
    : '+' | '-' | '*' | '/' | '%'
    ;

unaryOperator
	: '+' | '-'
	;

comparisonOperator
	:  '=' | '!=' | '<' | '<=' | '>' | '>='
	;

logicalOperator
	: 'and' | 'or'
    ;

BOOLEAN
    : 'true' | 'false'
    ;

STRING :  '"' (ESC | ~["\\])* '"' | '\'' (ESC | ~["\\])* '\'' ;

fragment ESC :   '\\' (["\\/bfnrt] | UNICODE) ;
fragment UNICODE : 'u' HEX HEX HEX HEX ;
fragment HEX : [0-9a-fA-F] ;

ID :  [a-zA-Z][a-zA-Z_0-9]* ;

NUMBER
    :   '-'? INT '.' [0-9]+ // 1.35, 0.3, -4.5
    |   '-'? INT           // -3, 45
    ;
fragment INT :   '0' | [1-9] [0-9]* ; // no leading zeros
//fragment EXP :   [Ee] [+\-]? INT ; // \- since - means "range" inside [...]

LPAREN : '(' ;
RPAREN : ')' ;
WS  :   [ \t\n\r]+ -> skip ;
COMMA : ',' ;
