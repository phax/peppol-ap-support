Running (PW is provided as env var):
```
docker run -d --name orcl19c -p 1521:1521 -p 5500:5500 -e ORACLE_PWD=password -v OracleDBData:/opt/oracle/oradata container-registry.oracle.com/database/enterprise:19.19.0.0
```

One time initialization:
```
-- Run this as SYS or SYSTEM in the PDB (e.g. ORCLPDB1)
-- Connect with: sqlplus sys/password@localhost:1521/ORCLPDB1 as sysdba

-- Unquote names get uppercases anyway
CREATE USER PEPREP IDENTIFIED BY peprep;

ALTER SESSION SET CURRENT_SCHEMA = PEPREP;

GRANT CREATE SESSION TO PEPREP;
GRANT CREATE TABLE TO PEPREP;
GRANT CREATE SEQUENCE TO PEPREP;
GRANT CREATE TRIGGER TO PEPREP;
GRANT CREATE VIEW TO PEPREP;
GRANT CREATE PROCEDURE TO PEPREP;
GRANT CREATE TYPE TO PEPREP;
GRANT CREATE MATERIALIZED VIEW TO PEPREP;

-- Optional: Unlimited quota on default tablespace (common for dev)
GRANT UNLIMITED TABLESPACE TO PEPREP;
```
