grammar Sagitarii;

activitySentence : OPERATOR executor '(' inputSentence (',' inputSentence)* ')' TO output_relation AS activity_name (';' parallelActivitySentence)*;

inputSentence :  input_relation | activitySentence;
parallelActivitySentence : activitySentence;

executor : TEXT;
input_relation : TEXT;
output_relation : TEXT;
activity_name : TEXT;


// Lexer Rules
OPERATOR : 'map' | 'split' | 'reduce' | 'select';
TO : 'to' ;
AS : 'as' ;
TEXT : ('a'..'z' | '0'..'9' | 'A'..'Z')+ ;
WHITESPACE : ( '\t' | ' ' | '\r' | '\n'| '\u000C' )+ -> skip ;
