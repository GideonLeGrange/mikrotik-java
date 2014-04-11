/** A Mikrotik API Command grammar */

grammar Command;		
line:       command param* where? retrn? ;
command:    CMD ;
param:      NAME ('=' text )? ;
text:       (QUOTED|VALUE) ;
where:      'where' expr ;
expr:       ( eqExpr | hasExpr | moreExpr | lessExpr ) binExpr? ;
binExpr:    andExpr | orExpr ;
eqExpr:     NAME '=' text ;
hasExpr:    NAME ;
moreExpr:   NAME '>' text; 
lessExpr:   NAME '<' text;
andExpr:    expr 'and' expr ;
orExpr:     expr 'or'  expr ;
retrn:    'returns' NAME (',' NAME )* ;

NAME     : ('a'..'z'|'A'..'Z'|'-')+ ;
CMD      : (('/')(NAME))+ ;
QUOTED   : ('"')(.*?)('"') ;
VALUE    : (~[ \t\r\n="])+  ;
WS: [ \n\t\r]+ -> skip;