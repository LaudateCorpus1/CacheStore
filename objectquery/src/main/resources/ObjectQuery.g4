grammar ObjectQuery;

script
    : (updateStatement+ | selectStatement) EOF
    ;

selectStatement
    : 'select' objectField (',' objectField)* 'from' identifier whereStatement? #SelectStats
    ;

updateStatement
    : ('replace'|'update') objectField 'set' assignments whereStatement? ';'?   #ReplaceStats
    | 'insert'  objectField 'set' assignments whereStatement? ';'?   #InsertStats
    ;

assignments
    : assignments ',' assignments    #AssignStats
    | objectField '=' value          #AssignObject
    ;

whereStatement
    : 'where' 'not'? searchCondition ;

searchCondition
    : objectField '=' (STRING | NUMBER | BOOLEAN | 'null')  #SearchObjectId
    | 'record#' '=' NUMBER                                  #SearchRecord
    ;


objectField
    : ID ('.'ID)*
    ;

identifier
    :  ID ;

object
    :   '{' assignments '}'           #ObjectAssigns
    |   '{' '}'                       #ObjectEmpty
    ;

array
    :   '[' value (',' value)* ']'   #ArrayValue
    |   '[' ']'                      #ArrayEmpty
    ;

value
    :   STRING                      #Strings
    |   NUMBER                      #Numbers
    |   object                      #Objects
    |   array                       #Arrays
    |   BOOLEAN                     #Booleans
    |   'null'                      #Nulls
    ;

BOOLEAN
    : 'true' | 'false'
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

WS  :   [ \t\n\r]+ -> skip ;
COMMA : ',' ;
//DOT : '.';
