#!/usr/bin/env bash

# uncomment for localhost
#PGOPTIONS="-c client_min_messages=WARNING"
#dropdb --if-exists -U enterprisedb jdbc_test
#createdb -U enterprisedb jdbc_test
#edb-psql -U enterprisedb -d jdbc_test -f create-edb.sql
#export PGPASSWORD="p4ssw0rd"
#edb-psql -U jdbc_test -d jdbc_test -f create.sql

# uncomment for remote host
export PGOPTIONS="-c client_min_messages=WARNING"
export PGPASSWORD="${jdbc_test.edb.password}"
dropdb --if-exists -h ${jdbc_test.edb.host} -p ${jdbc_test.edb.port} -U enterprisedb jdbc_test
createdb -h ${jdbc_test.edb.host} -p ${jdbc_test.edb.port} -U enterprisedb jdbc_test
edb-psql -h ${jdbc_test.edb.host} -p ${jdbc_test.edb.port} -U enterprisedb -d jdbc_test -f create-edb.sql
export PGPASSWORD="p4ssw0rd"
edb-psql -h ${jdbc_test.edb.host} -p ${jdbc_test.edb.port} -U jdbc_test -d jdbc_test -f create.sql