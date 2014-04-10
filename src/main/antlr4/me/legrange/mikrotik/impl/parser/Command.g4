/** A Mikrotik API Command grammar */

grammar Command;		
line:       command param* where? retrn? ;
command:    CMD ;
param:      NAME ('=' text )? ;
text:       NO_SPACE | QUOTED ;
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
CMD_PART : ('/'  NAME)+ ;
CMD      : CMD_PART+  ;
NO_SPACE : [\\S]+ ;
QUOTED   : ('"'  [.]*  '"') ;