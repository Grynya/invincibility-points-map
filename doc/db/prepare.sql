CREATE USER 'invPointMap'@'%' IDENTIFIED BY 'invPointMap';

GRANT ALL PRIVILEGES ON *.* TO 'invPointMap'@'%' WITH GRANT OPTION;

FLUSH PRIVILEGES;

create database invPointMap;
use invPointMap;