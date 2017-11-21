LEFTMAT = LOAD '$M' USING PigStorage(',') AS (row:int,col:int,value:double);
RIGHTMAT = LOAD '$N' USING PigStorage(',') AS (row:int,col:int,value:double);

Com = JOIN LEFTMAT BY col FULL OUTER, RIGHTMAT BY row;

Prod = FOREACH Com GENERATE LEFTMAT::row AS r1, RIGHTMAT::col AS c2, (LEFTMAT::value)*(RIGHTMAT::value) AS value;

Seg = GROUP Prod BY (r1, c2);

Ans = FOREACH Seg GENERATE group.$0 as row, group.$1 as col, SUM(Prod.value) AS val;

STORE Ans INTO '$O'USING PigStorage(',');
