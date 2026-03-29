# Architecture

## Module Layout

Three Gradle modules:
- `app/` — Spring Boot application (CXF SOAP endpoints, REST controllers, in-memory repositories)
- `domain/` — Pure domain logic, no Spring dependencies (behavior engine, navigation)
- `integration-test/` — E2E tests, depends on the `app` JAR

`app/build.gradle` has `jar { enabled = true }` so the plain JAR is available to `integration-test`.

## Hexagonal Layers (within `app`)

```
app/src/main/java/.../
├── application/          # Use cases + SOAP endpoints
│   ├── <servicename>/    # One package per SOAP service (no cross-dependencies)
│   └── common/           # Shared application logic (cross-service)
└── infrastructure/       # Adapters, config, persistence
    ├── config/           # Spring configuration, CXF wiring
    ├── passthrough/      # Optional per-service mTLS passthrough
    └── repository/       # AbstractInMemoryRepository implementations
```

Feature packages under `application/` must not depend on each other. Cross-service logic belongs in `common/`.

## Per-Service Layer Pattern

Every SOAP service (`registercertificate`, `revokecertificate`, `sendmessagetorecipient`, `statusupdates`, `storelog`) follows this structure:

| Class | Annotation | Responsibility |
|---|---|---|
| `*ResponderImpl` | `@Service` | SOAP plumbing only — delegates to `*Service`, builds RIV-TA response |
| `*Service` | `@Service` | All business logic — stores, converts, logs, filters, normalises IDs |
| `*Controller` | `@RestController` | HTTP plumbing only — delegates to `*Service`, maps to HTTP responses |
| `*Converter` | `@Component` | Converts SOAP JAXB types → DTO |
| `*Repository` | `@Repository` | Extends `AbstractInMemoryRepository<T>` |
| `*DTO` | `@Value @Builder` | Lombok data object, immutable |

## CxfConfig Wiring

`CxfConfig` manually instantiates `*ResponderImpl` beans and publishes them as CXF endpoints.

- New services must be manually registered in `CxfConfig.java`
- All SOAP paths prefixed `/services/`
- Endpoint path convention: `/services/<domain>/<service>/<version>/rivtabp21`
- Responder impls are annotated `@Service` even though also manually instantiated in `CxfConfig`

## Common Infrastructure

- `AbstractInMemoryRepository<T>` — generic in-memory store with configurable max-size and FIFO eviction
  - Methods: `add`, `findAll`, `findByKey`, `deleteAll`, `removeIf(Predicate<T>)`
- `common/converter/` — shared converters for `Intyg`, `Patient`, etc. used across services
- Repository max sizes configurable via `app.repository.<service>.max-size` (default 1000)

## Navigation Layer

HATEOAS controllers in `domain/navigation/` assemble links from per-service repositories. They do not own data — they only read from repositories to build link structures.

## Architecture Tests

`ArchitectureTest.java` in the test sources mechanically enforces the layer rules above. Run `./gradlew :app:test` to verify.
