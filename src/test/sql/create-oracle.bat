:: uncomment for localhost
::sqlplus -S / as sysdba @ create-oracle.sql
::sqlplus -S jdbc_test/p4ssw0rd @ create.sql

:: uncomment for remote host
sqlplus -S sys/${jdbc_test.oracle.password}@${jdbc_test.oracle.host}:${jdbc_test.oracle.port}/${jdbc_test.oracle.service_name} as sysdba @create-oracle.sql
sqlplus -S jdbc_test/p4ssw0rd@${jdbc_test.oracle.host}:${jdbc_test.oracle.port}/${jdbc_test.oracle.service_name} @create.sql