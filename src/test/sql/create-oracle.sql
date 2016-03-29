/* MUST BE CONNECTED AS SUPERUSER */
DROP USER jdbc_test CASCADE;
CREATE USER jdbc_test
IDENTIFIED BY p4ssw0rd
DEFAULT TABLESPACE users
TEMPORARY TABLESPACE temp
QUOTA 10M ON users;

GRANT CONNECT TO jdbc_test;
GRANT RESOURCE TO jdbc_test;
GRANT CREATE SESSION TO jdbc_test;
GRANT CREATE TABLE TO jdbc_test;
GRANT CREATE VIEW TO jdbc_test;

CREATE OR REPLACE FUNCTION jdbc_test.test_error(message VARCHAR)
  RETURN INTEGER IS
  BEGIN
    raise_application_error(-20000, message);
    RETURN 0;
  END;
/
COMMIT;

EXIT;