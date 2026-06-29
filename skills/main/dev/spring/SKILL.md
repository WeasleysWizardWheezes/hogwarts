---
name: spring
description: Nutze diesen Skill beim Schreiben, Ändern, Refactoren oder Reviewen von Spring-Boot-Code, insbesondere bei Dependency Injection, Konfiguration, Profilen, Transaktionen, Bean Validation, Spring Data JPA, Maven und Integrationstests mit Testcontainers.
---

# Spring

Dieser Skill definiert Spring-Boot-spezifische Regeln. Allgemeine Java-Regeln stehen im Java-Skill. Architektur, Package-Struktur und Schichtentrennung stehen in den Code-Richtlinien.

## Projektstandard

* Verwende Spring Boot mit Maven als Build-Tool.
* Verwende PostgreSQL als relationale Datenbank.
* Verwende Spring Data JPA für persistenznahe Infrastruktur.
* Verwende Testcontainers für Integrationstests gegen PostgreSQL.
* Verwende Profile für unterschiedliche Laufzeitumgebungen.

---

## Dependency Injection

* Verwende Constructor Injection.
* Verwende keine Field Injection mit `@Autowired`.
* Bei genau einem Konstruktor ist `@Autowired` am Konstruktor nicht nötig.
* Abhängigkeiten sollen über Konstruktorparameter sichtbar sein.
* Optionale Abhängigkeiten nur verwenden, wenn sie fachlich notwendig sind.

Gut:

```java
@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }
}
```

Schlecht:

```java
@Service
public class EquipmentService {

    @Autowired
    private EquipmentRepository equipmentRepository;
}
```

---

## Konfiguration

* Verwende `application.yml` für Default-Konfiguration.
* Verwende `application-local.yml` für lokale Entwicklung.
* Umgebungsspezifische Werte werden über Profile getrennt.
* Strukturierte Konfiguration wird über `@ConfigurationProperties` abgebildet.
* Verwende `@Value` nicht für zusammengehörige Konfigurationsgruppen.
* Secrets gehören nicht fest in Konfigurationsdateien.

Gut:

```java
@ConfigurationProperties(prefix = "app.storage")
public record StorageProperties(
        String basePath,
        long maxFileSize
) {
}
```

Schlecht:

```java
@Value("${app.storage.base-path}")
private String basePath;

@Value("${app.storage.max-file-size}")
private long maxFileSize;
```

---

## Profile

* Default-Werte liegen in `application.yml`.
* Lokale Entwicklungswerte liegen in `application-local.yml`.
* Testwerte liegen, falls nötig, in `application-test.yml`.
* Produktionswerte werden nicht hart im Repository hinterlegt.
* Profile dürfen keine Architekturunterschiede erzwingen, sondern nur Konfiguration austauschen.

Beispiel:

```yaml
spring:
  profiles:
    active: local
```

---

## Transaktionen

* Verwende `@Transactional` explizit auf Service-, Application-Service- oder Use-Case-Ebene.
* Transaktionen gehören nicht in Controller.
* Schreiboperationen verwenden normale Transaktionen.
* Reine Leseoperationen können `@Transactional(readOnly = true)` verwenden.
* Transaktionsgrenzen sollen fachliche Anwendungsfälle abbilden.
* Vermeide unnötig große Transaktionen.
* Achte darauf, dass interne Methodenaufrufe innerhalb derselben Klasse nicht zuverlässig über Spring-Proxies transaktional abgefangen werden.

Gut:

```java
@Service
public class EquipmentApplicationService {

    @Transactional
    public EquipmentResponse createEquipment(CreateEquipmentRequest request) {
        // use case
    }

    @Transactional(readOnly = true)
    public EquipmentResponse getEquipment(UUID equipmentId) {
        // query
    }
}
```

Schlecht:

```java
@RestController
public class EquipmentController {

    @Transactional
    @PostMapping("/api/v1/equipment")
    public EquipmentResponse createEquipment(@RequestBody CreateEquipmentRequest request) {
        // controller logic
    }
}
```

---

## Bean Validation

* Verwende Bean Validation am Controller-Eingang.
* Verwende `@Valid` für Request-Bodies.
* Verwende passende Constraints wie `@NotNull`, `@NotBlank`, `@Size`, `@Min`, `@Max` oder `@Email`.
* Technische Request-Validierung gehört an DTOs.
* Fachliche Validierung gehört nicht in Bean-Validation-Annotationen, sondern in die Anwendungs- oder Domainlogik.

Beispiel:

```java
public record CreateEquipmentRequest(
        @NotBlank String name,
        @NotBlank String serialNumber
) {
}
```

```java
@PostMapping
public ResponseEntity<EquipmentResponse> createEquipment(
        @Valid @RequestBody CreateEquipmentRequest request
) {
    // delegate to application service
}
```

---

## Spring Data JPA

* Verwende Spring Data JPA in der Infrastruktur-Schicht.
* Spring-Data-Repositories dürfen technische Persistenzdetails enthalten.
* Domain- oder Application-Code soll nicht direkt von `JpaRepository` abhängen.
* Verwende aussagekräftige Query-Methoden.
* Komplexe Queries sollen bewusst modelliert werden, statt in unlesbaren Methodennamen zu enden.
* Lazy Loading darf nicht unkontrolliert bis in die API-Schicht leaken.

Gut:

```java
interface JpaEquipmentRepository extends JpaRepository<EquipmentJpaEntity, UUID> {

    Optional<EquipmentJpaEntity> findBySerialNumber(String serialNumber);
}
```

---

## REST-Integration

* Controller validieren Requests und delegieren an Application Services oder Use Cases.
* HTTP-spezifische Details bleiben in der API-Schicht.
* Exception Mapping erfolgt zentral über `@RestControllerAdvice`.
* Das konkrete Fehlerformat folgt dem API-Guidelines-Skill.
* API-DTOs werden nicht in Domain-Objekten verwendet.

---

## Integrationstests

* Verwende Testcontainers für Integrationstests mit PostgreSQL.
* Integrationstests sollen gegen eine echte PostgreSQL-Testdatenbank laufen.
* Testdaten sollen explizit im Test vorbereitet werden.
* Tests dürfen sich nicht auf lokale Datenbanken verlassen.
* Tests müssen unabhängig voneinander ausführbar sein.
* Datenbankzustand zwischen Tests muss kontrolliert werden.

Beispiel:

```java
@SpringBootTest
@Testcontainers
class EquipmentIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Test
    void should_create_equipment() {
        // arrange
        // act
        // assert
    }
}
```

---

## Checkliste

Vor Abschluss einer Spring-Änderung prüfen:

* [ ] Constructor Injection wird verwendet.
* [ ] Es gibt keine Field Injection mit `@Autowired`.
* [ ] Konfiguration nutzt `@ConfigurationProperties` für zusammengehörige Werte.
* [ ] Profile sind sauber getrennt.
* [ ] Transaktionen liegen nicht im Controller.
* [ ] Schreib- und Leseoperationen haben passende Transaktionsgrenzen.
* [ ] Request-DTOs verwenden Bean Validation.
* [ ] Spring Data JPA bleibt in der Infrastruktur.
* [ ] Integrationstests verwenden Testcontainers für PostgreSQL.
* [ ] HTTP-spezifische Details leaken nicht in Domain oder Application.
