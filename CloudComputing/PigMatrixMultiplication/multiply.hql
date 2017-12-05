drop table LeftMatrix;
drop table RightMatrix;
drop table LRMatrix;

create table LeftMatrix (
  mrow int,
  mcol int,
  mval double)
row format delimited fields terminated by ',' stored as textfile;

create table RightMatrix (
  nrow int,
  ncol int,
  nval double)
row format delimited fields terminated by ',' stored as textfile;

create table LRMatrix (
  mnrow int,
  mncol int,
  mnval double)
row format delimited fields terminated by ',' stored as textfile;

load data local inpath '${hiveconf:M}' overwrite into table LeftMatrix;
load data local inpath '${hiveconf:N}' overwrite into table RightMatrix;

INSERT OVERWRITE TABLE LRMatrix
select m.mrow,n.ncol,sum(m.mval*n.nval)
from LeftMatrix as m full outer join RightMatrix as n on m.mcol = n.nrow
GROUP BY m.mrow,n.ncol;

Select count(mnrow),avg(mnval) from LRMatrix;

