DB2 authenticates against operating system users of the container - it has no internal user
management. The image only creates the instance owner `db2inst1`, so the `peppol` user from
`application-db2.properties` must be created manually, once per container.

Wait until the container is healthy - the initial DB2 setup takes several minutes:
```
docker compose -f unittest-db-docker-compose.yml ps db2
```

One time initialization:
```
-- Create the OS user; it must be in the db2iadm1 group to be accepted by DB2
docker exec peppol-ap-support-db2-1 bash -c "useradd -m -s /bin/bash -g db2iadm1 peppol && echo 'peppol:peppol' | chpasswd"

-- Flyway creates the "report" schema and all tables, so DBADM is needed
docker exec peppol-ap-support-db2-1 su - db2inst1 -c "db2 connect to pepprep && db2 'GRANT DBADM ON DATABASE TO USER PEPPOL' && db2 connect reset"
```

Verification - the output must show `SQL authorization ID = PEPPOL`:
```
docker exec peppol-ap-support-db2-1 su - db2inst1 -c "db2 connect to pepprep user peppol using peppol && db2 connect reset"
```

Notes:
* `docker compose down` removes the container and therefore the OS user - the initialization
  must then be repeated. Stopping and starting the container preserves it.
* Without the OS user the test fails with `ERRORCODE=-4214, SQLSTATE=28000 - User ID or
  Password invalid`.

  
## Old

```shell
docker volume create db2_data
```

* DB2 is memory hungry

```shell
docker run -itd --name db2-peppol-ap-support --platform=linux/amd64 --privileged=true --memory=4g -p 50000:50000 -e LICENSE=accept -e DB2INST1_PASSWORD=peppol -e DBNAME=pepprep -v db2_data:/database icr.io/db2_community/db2
```

Required to create initial user and grant access to the DB "smp"

```shell
docker exec -ti db2-peppol-ap-support bash -c "useradd peppol && echo 'peppol:peppol' | chpasswd"

docker exec -ti db2-peppol-ap-support bash -c "su - db2inst1 -c \"
  db2 connect to pepprep;
  db2 GRANT DBADM ON DATABASE TO USER peppol;
  db2 connect reset;
\""
```

After some troubles, DB2 refused to start up - this helped:
```shell
docker exec -ti db2-peppol-ap-support bash -c "chown root:db2iadm1 /database/config/db2inst1/sqllib/adm/fencedid"
```
