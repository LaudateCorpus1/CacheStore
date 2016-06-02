grammar Query;

script
    : (updateStatement | selectStatement) EOF
    ;

selectStatement
    : 'select' objectField (',' objectField)* 'from' identifier whereStatement? limitCause?  #SelectStats
    ;

updateStatement
    : 'replace' objectField 'set' assignments whereStatement?    #ReplaceStats
    | 'insert'  objectField 'set' assignments whereStatement?    #InsertStats
    ;



whereStatement
    : 'where' 'not'? predicate
    ;

limitCause
    : 'limit' NUMBER                     #LimitPhrase
    ;

predicate
	: predicate logicalOperator predicate  #LogicPredicate
	| LPAREN predicate RPAREN              #ParenPredicate
	| 'not' predicate                      #NotPredicate
	| objectPredicate                      #ObjPredicate
    ;

objectPredicate
    : objectField comparisonOperator expression   #Comparison
    | objectField 'in' LPAREN expression (COMMA expression)* RPAREN  #InComp
    | expression comparisonOperator expression    #ExpressionPredicate
    ;

expression
    : expression binaryOperator expression   #Expr
    | LPAREN expression RPAREN               #ExpParen
    | functionalExpression                   #FunctionExp
    | value                                  #ExpValues
    ;

functionalExpression
    : 'lower' LPAREN objectField RPAREN  #LowerExpr
    | 'upper' LPAREN objectField RPAREN  #UpperExpr
    | 'count' LPAREN objectField ',' (objectField ',')? expression RPAREN #CountExpr
    | 'substr' LPAREN objectField ',' expression RPAREN #SubstrExpr
    | 'exist' LPAREN objectField ',' (objectField ',')? expression RPAREN #ExistExpr
    | 'strToBytes' LPAREN STRING RPAREN  #StrToBytesFuncExpr
    ;

assignments
    : assignments ',' assignments    #AssignStats
    | objectField '=' expression     #AssignObject
    ;

objectField
    : 'key#'                         #Keys
    | '*'                            #All
    | ID ('.'ID)*                    #IDS
    ;

identifier
    :  ID ;


value
    :  object                      #Objects
    |  array                       #Arrays
    |  STRING                      #Strings
    |  NUMBER                      #Numbers
    |  BOOLEAN                     #Booleans
    |  'null'                      #Nulls
    ;

object
    :   '{' assignments '}'           #ObjectAssigns
    |   '{' '}'                       #ObjectEmpty
    ;

array
    :   '[' value (',' value)* ']'   #ArrayValue
    |   '[' ']'                      #ArrayEmpty
    ;

BOOLEAN
    : 'true' | 'false'
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

STRING :  '"' (ESC | ~["\\])* '"' ;

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

