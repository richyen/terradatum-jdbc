:: uncomment for localhost
::SET "PGOPTIONS=-c client_min_messages=WARNING"
::dropdb --if-exists -U enterprisedb jdbc_test
::createdb -U enterprisedb jdbc_test
::psql -U enterprisedb -d jdbc_test -f create-edb.sql
::SET PGPASSWORD="p4ssw0rd"
::psql -U jdbc_test -d jdbc_test -f create.sql

:: uncomment for remote host
SET PGOPTIONS="-c client_min_messages=WARNING"
SET PGPASSWORD="${jdbc_test.edb.password}"
dropdb --if-exists -h ${jdbc_test.edb.host} -p ${jdbc_test.edb.port} -U enterprisedb jdbc_test
createdb -h ${jdbc_test.edb.host} -p ${jdbc_test.edb.port} -U enterprisedb jdbc_test
psql -h ${jdbc_test.edb.host} -p ${jdbc_test.edb.port} -U enterprisedb -d jdbc_test -f create-edb.sql
SET PGPASSWORD="p4ssw0rd"
psql -h ${jdbc_test.edb.host} -p ${jdbc_test.edb.port} -U jdbc_test -d jdbc_test -f create.sql