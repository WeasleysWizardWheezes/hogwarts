---
name: tech-stack
description: Verwende wenn du Code schreibst, Dependencies hinzufügst, Build-Konfiguration änderst oder Versionskompatibilität prüfen musst
---

# Tech-Stack

## Sprache & Runtime
- Java 25

## Framework
- Spring Boot 4.1.0

## Build
- Maven

## Projekt
- GroupId: de.thkoeln.ccq
- ArtifactId: firemanager
- Package: de.thkoeln.ccq.firemanager

## Dependencies
| Dependency | Version | Zweck |
|-----------|---------|-------|
| spring-boot-starter-webmvc | (managed) | Web/REST |
| spring-boot-starter-validation | (managed) | Bean Validation |
| spring-boot-starter-data-jpa | (managed) | Persistenz |
| jakarta.persistence-api | (managed) | JPA API |
| postgresql | (managed) | Datenbank-Treiber |
| Lombok | 1.18.40 | Boilerplate-Reduktion |
| JaCoCo | 0.8.13 | Code Coverage |
| Checkstyle (Maven Plugin) | 3.6.0 | Code Style Enforcement |
| PITest | 1.19.6 | Mutation Testing |
| pitest-junit5-plugin | 1.2.2 | PITest JUnit5 Support |
| Testcontainers (PostgreSQL) | (managed) | Integrationstests mit DB |
| Mockito | (managed) | Mocking in Unit Tests |

## Konfigurationspfade
- **Checkstyle:** `config/checkstyle/checkstyle.xml`
- **JaCoCo:** Report in `target/site/jacoco/`
- **PITest:** Ziel-Package `de.thkoeln.ccq.firemanager.**`
- **Application Config:** `src/main/resources/application.properties`

## Dependency-Management
- Managed Versionen (via Spring Boot Parent) bevorzugen
- Nur explizite Version setzen wenn nötig (wie Lombok, PITest)
- Keine SNAPSHOT-Versionen
- Neue Dependencies immer mit konkretem Zweck begründen

## Wichtige Constraints
- Java 25 Features nutzen
- Spring Boot Starter bevorzugen statt manuelle Config
- Tests mit Testcontainers (PostgreSQL) für Integrationstests
- Mockito für Unit Tests

## Build-Befehle
- Build + Tests + Quality Gates: `mvn verify`
- Nur Tests: `mvn test`
- Mutation Testing: `mvn pitest:mutationCoverage`
- Checkstyle allein: `mvn checkstyle:check`
- Anwendung starten: `mvn spring-boot:run`

## Runtime-Umgebung
- Datenbank: PostgreSQL (lokal via Testcontainers, prod via Docker)
- Port: 8080 (default)

## Container-Umgebung (Agent)
- **Java 25 ist vorinstalliert.** `JAVA_HOME` ist gesetzt, `java` und `mvn` (via Maven Wrapper) sind im PATH.
- **PostgreSQL läuft NICHT im Container.** Integrationstests nutzen Testcontainers (braucht Docker-Socket). Falls `mvn test` wegen fehlender DB scheitert → Code pushen und auf die GitHub Actions Pipeline verlassen.
- Build lokal ausführbar: `chmod +x mvnw && ./mvnw verify`

## Kompatibilitätshinweise
- Spring Boot 4.x erfordert Jakarta EE 10 (jakarta.* statt javax.*)
- Lombok 1.18.40 ist kompatibel mit Java 25
- Testcontainers erfordert Docker auf dem Host

## Kompatibilitäts-Prüfung (Online-Ressourcen)
- Maven Central Search: https://search.maven.org (neueste Versionen, Artefakt-Existenz)
- Spring Boot Managed Versions: https://docs.spring.io/spring-boot/docs/4.0.x/reference/html/dependency-versions.html
- Spring Initializr: https://start.spring.io (kompatible Dependencies pro Boot-Version)
- MVNRepository: https://mvnrepository.com (Abhängigkeiten zwischen Libraries, Compile-Deps)

## Referenz-Dokumentation
- Java 25: https://docs.oracle.com/en/java/javase/25/docs/api/
- Spring Boot 4.1: https://docs.spring.io/spring-boot/4.1/documentation.html
- Checkstyle: https://checkstyle.org/checks.html
- JaCoCo: https://www.jacoco.org/jacoco/trunk/doc/
- PITest: https://pitest.org/quickstart/maven/ und https://pitest.org/quickstart/basic_concepts/
- Mockito: https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito/org/mockito/Mockito.html
- Testcontainers: https://java.testcontainers.org/
- Lombok: https://projectlombok.org/features/
