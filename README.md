# Peppol AP Support

Contains some general supporting functionality for Access Points.
This is a utility component for phase4 common Access Point implementation components. It will grow over time.

This library uses Java 17 as the baseline.

It is licensed under the Apache 2.0 license.

It consists of the following submodules:
* peppol-ap-support
* peppol-reporting-support

# peppol-ap-support

Contains a Business Card cache, that makes sure Business Card of a Peppol Participant are locally cached and not always queried remotely.

# peppol-reporting-support

The Peppol Reporting support library offers simple ways to validate, store and send Peppol TSR and EUSR reports.
It support different backends based on the `IPeppolReportStorage` interface.
Custom forms of this interface may be created and used.

## MongoDB backend

Allows to store data in collections `peppol-reports` and `peppol-reporting-sending-reports`.
You may either use the same database as you use for Peppol Reporting (see the [peppol-reporting](https://github.com/phax/peppol-reporting) project) or define explicit parameters.

## SQL backend

For Peppol Report storage SQL backend supports the following configuration properties:
* **`peppol.report.jdbc.database-type`**: the SQL database type to operate on. Currently supported are `postgresql` and `mysql`. The value is case-insensitive.
* **`peppol.report.jdbc.driver`**: contains the fully qualified class name of the JDBC driver to be used. E.g. `org.postgresql.Driver` for PostgreSQL or `com.mysql.cj.jdbc.Driver` for MySQL
* **`peppol.report.jdbc.url`**: contains the full JDBC connection URL to connect to the database
* **`peppol.report.jdbc.user`** (optional): the database username to use
* **`peppol.report.jdbc.password`** (optional): the database password to use
* **`peppol.report.jdbc.schema`** (optional): the database schema to use
* **`peppol.report.jdbc.execution-time-warning.enabled`** (optional):  if `true` enables warning logging if an SQL command takes too long to execute. Defaults to `true`.
* **`peppol.report.jdbc.execution-time-warning.ms`** (optional): the number of milliseconds after the which an SQL execution will trigger an execution time warning. Defaults to `1000` which is one second.
* **`peppol.report.jdbc.debug.connections`** (optional):  if `true` enables logging of SQL connection handling. Defaults to `false`.
* **`peppol.report.jdbc.debug.transactions`** (optional): if `true` enables logging of SQL transactions. Defaults to `false`. 
* **`peppol.report.jdbc.debug.sql`** (optional): if `true` enables logging of SQL statements. Defaults to `false`.

Database change management is done with the Open Source version of Flyway.
All the Flyway DDL scripts are available in the folder https://github.com/phax/peppol-ap-support/tree/main/peppol-reporting-support/src/main/resources/db

It can be configured as followed:
* **`peppol.report.flyway.enabled`**: `true` if Flyway should be enabled, `false` if not. Defaults to `true`.
* **`peppol.report.flyway.jdbc.url`** (optional): allows a specific JDBC URL for usage with Flyway. If none is provided, the value of `peppol.report.jdbc.url` is used instead.
* **`peppol.report.flyway.jdbc.user`** (optional): allows a specific JDBC username for usage with Flyway. If none is provided, the value of `peppol.report.jdbc.user` is used instead.
* **`peppol.report.flyway.jdbc.password`** (optional): allows a specific JDBC password for usage with Flyway. If none is provided, the value of `peppol.report.jdbc.password` is used instead.
* **`peppol.report.flyway.jdbc.schema-create`** (optional): `true` if the DB schema as defined in `peppol.report.jdbc.schema` should be automatically created by Flyway. Defaults to `false`.
* **`peppol.report.flyway.baseline.version`** (optional): the Flyway baseline version to use. Defaults to `0`.

## File backend

Allows to store Peppol Reports as well as Peppol Reporting Sending reports on disk, in a customizable folder structure.
All information are stored in a custom XML format. 

# News and noteworthy

v2.1.0 - 2025-11-16
* Updated to ph-commons 12.1.0
* Using JSpecify annotations

v2.0.2 - 2025-10-27
* Enforcing the usage of DNS NAPTR lookup - no more configuration possible

v2.0.1 - 2025-09-19
* [SQL] Updated to ph-db 8.0.1

v2.0.0 - 2025-08-27
* Requires Java 17 as the minimum version
* Updated to ph-commons 12.0.0
* [SQL] Updated to Flyway 11.x

v1.0.2 - 2025-05-11
* Updated to peppol-commons 10.3.2
* Added new class `MLRSupportCache` to check if business document senders support MLR document type or not
* Added new class `MLSSupportCache` to check if business document senders support MLS document type or not

v1.0.1 - 2025-04-12
* Make sure, the created timestamps only use millisecond precision

v1.0.0 - 2025-04-12
* Initial version extracted from peppol-commons module as submodule peppol-ap-support
* The initial package is now `com.helper.peppol.apsupport` to clearly differentiate from the old one

---

My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodingStyleguide.md) |
It is appreciated if you star the GitHub project if you like it.
