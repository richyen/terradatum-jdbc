#BEWARE

These scripts create/drop users/schema/databases - run with due consideration.

They create the following on both Oracle and PPAS:
1. `jdbc_test` database and role on PPAS
2. `jdbc_test` user/schema
3. Sequences, Tables, Constraints, Objects, Triggers and Packages used by the tests

Set the following properties in your pom.xml or other property-replacement mechanism, or replace the values manually.
```bash
jdbc_test.oracle.host
jdbc_test.oracle.port
jdbc_test.oracle.service_name
jdbc_test.oracle.password # sys as sysdba password
jdbc_test.edb.host
jdbc_test.edb.port
jdbc_test.edb.password # enterprisedb password
```

You don't need the above properties if you run the scripts on the localhost. To do that, you'll need to modify the shell/batch
scripts accordingly.

For the shell scripts modified by your build process, look in `target/generated-sql-scripts` - you'll need to make them executable
via `chmod u+x` on nix.

The syntax between EDB and Oracle is _just_ enough different when running command scripts, that a choice had to be made to allow
for One warnings when running against EDB:
```
psql.bin:create-edb.sql:13: invalid command \q;
```
The reason for this is that `sqlplus` requires an `EXIT;` command at the end of the script in order to, well, exit... and `edb-psql`
sees this as an error. However, since the error occurs at the end of the script, it can be ignored.

##Oracle
###create-oracle.(bat|sh)
Shell script for nix and windows to execute the Oracle SQL scripts, and run `create.sql`.

###create-oracle.sql
Create the `jdbc_test` database, role and schema.

##EDB
You *MUST* have the EnterpriseDB version of `psql` installed for these scripts to behave properly. While the convention on an EDB
PPAS deployment is to use `psql`, these commands are deliberately using the wrapper script `edb-psql` to remove any possible ambiguity.

###create-edb.(bat|sh)
Shell script for nix and windows to drop/create `jdbc_test` database and execute the PPAS SQL scripts, and run `create.sql`.

###create-edb.sql
Run on PPAS host to create the `jdbc_test` database.

##Oracle and EDB
###create.sql
Creates the Sequences, Tables, Constraints, Objects, Triggers and Packages used by the tests.