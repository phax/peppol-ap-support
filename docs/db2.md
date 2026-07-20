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
