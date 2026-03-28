# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Peppol AP Support is a Java utility library for Peppol Access Point implementations, part of the broader [Peppol solution stack](https://github.com/phax/peppol). Licensed under Apache 2.0, Java 17 baseline.

Two Maven modules:
- **peppol-ap-support** — Business Card cache (`BusinessCardCache`), document type support caches (`MLRSupportCache`, `MLSSupportCache`)
- **peppol-reporting-support** — Validation, storage, and sending of Peppol TSR/EUSR reports with pluggable backends via `IPeppolReportStorage`: file-based XML (`file/`), MongoDB (`mongodb/`), SQL with Flyway migrations (`sql/`)

## Build Commands

```bash
# Full build with tests (requires databases running)
mvn install

# Start local databases for tests
docker compose -f unittest-db-docker-compose.yml up

# Build without tests
mvn install -DskipTests

# Run a single test class
mvn -pl peppol-reporting-support test -Dtest=PeppolReportStorageSQLTest

# Run a single test method
mvn -pl peppol-reporting-support test -Dtest=PeppolReportStorageSQLTest#testMethodName
```

## Test Database Requirements

Tests require MongoDB 6.0, PostgreSQL, and MySQL 8.0. Use the provided `unittest-db-docker-compose.yml` or start them manually. Expected credentials:
- **PostgreSQL**: user=peppol, password=peppol, database=peppol-report, port=5432
- **MySQL**: user=peppol, password=peppol, database=peppol-report, port=3306
- **MongoDB**: no auth, port=27017

## Architecture

### peppol-reporting-support storage backends

All backends implement `IPeppolReportStorage`. The SQL backend uses Flyway for schema management with DDL scripts in `src/main/resources/db/peppol-report-postgresql/` and `src/main/resources/db/peppol-report-mysql/`. SQL configuration is properties-driven (see README for `peppol.report.jdbc.*` and `peppol.report.flyway.*` properties).

### Key dependencies

- `com.helger.commons` (ph-commons) — collection types (`ICommonsList`, `CommonsArrayList`), utilities
- `com.helger.peppol` (peppol-commons) — Peppol domain types, SMP client
- `com.helger.peppol` (peppol-reporting) — TSR/EUSR marshalling and validation
- Flyway — SQL schema migrations
- OSGi bundle packaging via maven-bundle-plugin

### Package structure

- `com.helger.peppol.apsupport` — AP support caches
- `com.helger.peppol.reportingsupport` — Core reporting interfaces and `PeppolReportingSupport`
- `com.helger.peppol.reportingsupport.domain` — Data model classes
- `com.helger.peppol.reportingsupport.file` — File/XML storage backend
- `com.helger.peppol.reportingsupport.mongodb` — MongoDB storage backend
- `com.helger.peppol.reportingsupport.sql` — SQL storage backend (PostgreSQL/MySQL)
