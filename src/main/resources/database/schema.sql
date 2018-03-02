create table if not exists CLIENTS
(
  ID       bigint auto_increment,
  LOGIN    varchar(255) not null,
  PASSWORD varchar(255) not null,
  BALANCE  DOUBLE(3)    not null,
);