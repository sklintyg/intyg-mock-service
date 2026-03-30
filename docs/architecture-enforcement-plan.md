# Architecture Enforcement Plan

## Context

The codebase has 5 navigation repository implementations in `infrastructure/repository/navigation/` that import application-layer converters and DTOs — violating the hexagonal rule that infrastructure must not depend on application. Controllers and ResponderImpls also sit at feature package roots instead of `.api` subpackages, preventing package-based ArchUnit rules. The existing `ArchitectureTest.java` has good coverage but lacks rules for infra→app isolation and service layer purity.

## Findings

### Violation 1: Infrastructure imports application (5 files)

| Infrastructure class | Application imports |
|---|---|
| `CertificateNavigationRepositoryImpl` | `RegisterCertificateConverter`, `RegisterCertificateDTO`, `PatientDTO`, `HoSPersonalDTO` |
| `AuditLogEntryNavigationRepositoryImpl` | `StoreLogTypeConverter`, `LogTypeDTO` |
| `MessageNavigationRepositoryImpl` | `SendMessageToRecipientConverter`, `SendMessageToRecipientDTO` |
| `RevocationNavigationRepositoryImpl` | `RevokeCertificateConverter`, `RevokeCertificateDTO` |
| `StatusUpdateNavigationRepositoryImpl` | `CertificateStatusUpdateForCareConverter`, `CertificateStatusUpdateForCareDTO` |

**Root cause:** These repos convert JAXB types -> application DTOs -> domain models. The intermediate DTO step creates the dependency.

### Violation 2: Controllers not in `.api` subpackages

All controllers and ResponderImpls sit at `application.<feature>/` root. The target convention requires `application.<feature>.api/`.

### Violation 3: Missing ArchUnit rules

Rules 1, 2, 3 from the task are not enforced. Rule 4 (domain isolation) already exists.

## Refactoring Steps

### Step 1: Add `sourceXml` field to domain navigation models + create shared XML marshaller

**Domain model changes** (5 files in `domain/src/main/java/.../domain/navigation/model/`):
- `Certificate.java` — add `String sourceXml`
- `Message.java` — add `String sourceXml`
- `Revocation.java` — add `String sourceXml`
- `StatusUpdate.java` — add `String sourceXml`
- `AuditLogEntry.java` — add `String sourceXml`

**Create shared XML marshaller** in infrastructure:
- `infrastructure/xml/JaxbXmlMarshaller.java` — `@Component` that marshals any supported JAXB type to XML string
- Consolidates the duplicated `marshalToXml` pattern already in 5 service classes (each has its own `JAXBContext` + `Marshaller` boilerplate)
- Initialises a single `JAXBContext` covering all schema packages
- Public method: `String marshal(Object jaxbType)` that wraps with the correct `ObjectFactory` element and marshals to formatted XML string

### Step 2: Fix infra->app imports (5 navigation repo impls + their tests)

For each of the 5 files:

1. Remove the application converter constructor dependency
2. Replace application DTO imports with JAXB type imports (`se.riv.*`)
3. Rewrite `toX(DTO)` methods as `toX(JAXBType)` — convert directly from JAXB to domain model:
   - `dto.getIntygsId().getExtension()` -> `source.getIntygsId().getExtension()` (same path on JAXB)
   - `dto.getPatient().getPersonId()` -> `source.getPatient().getPersonId()` (same)
   - `dto.getSkapadAv().getEnhet()` -> `source.getSkapadAv().getEnhet()` (same)
4. Remove `.map(converter::convert)` from stream pipelines
5. Inject `JaxbXmlMarshaller` and set `sourceXml` when building domain objects
6. Update corresponding test classes to construct JAXB types instead of mocking converters

**Key files:**
- `app/.../infrastructure/repository/navigation/CertificateNavigationRepositoryImpl.java`
- `app/.../infrastructure/repository/navigation/AuditLogEntryNavigationRepositoryImpl.java`
- `app/.../infrastructure/repository/navigation/MessageNavigationRepositoryImpl.java`
- `app/.../infrastructure/repository/navigation/RevocationNavigationRepositoryImpl.java`
- `app/.../infrastructure/repository/navigation/StatusUpdateNavigationRepositoryImpl.java`
- Their 5 corresponding test classes in `app/src/test/`

**Note for `Certificate`:** The `buildMergedMap()` method already accesses JAXB types directly for stub entries from revocations/messages/statusupdates/storelog. Those stubs will have `sourceXml = null` since they're partial entries without a full RegisterCertificateType.

**Note for `AuditLogEntry`:** The source is `LogType` (inner element of `StoreLogType`), not the outer `StoreLogType` — marshal `LogType` specifically.

### Step 3: Move controllers and ResponderImpls to `.api` subpackages

For each SOAP feature (`registercertificate`, `revokecertificate`, `sendmessagetorecipient`, `statusupdates`, `storelog`):
- Move `*Controller.java` and `*ResponderImpl.java` -> `<feature>/api/`

For `behavior`:
- Move `BehaviorController.java` -> `behavior/api/`

For `reset`:
- Move `ResetController.java` -> `reset/api/`

For `navigation`:
- Move `RootNavigationController.java` -> `navigation/api/`
- Move each sub-feature `*Controller.java` -> `navigation/<sub>/api/`

After moving: update imports in `CxfConfig.java`, test classes, and any other references.

### Step 4: Add ArchUnit rules

Add to `ArchitectureTest.java`:

**Rule 1 — Services must not use Spring Web/Messaging:**
```java
noClasses().that().resideInAPackage("..application..service..")
    .should().dependOnClassesThat()
    .resideInAnyPackage("org.springframework.web..", "org.springframework.messaging..");
```

**Rule 2 — API classes must not import domain:**
```java
noClasses().that().resideInAPackage("..application..api..")
    .should().dependOnClassesThat()
    .resideInAPackage("..domain..");
```

**Rule 3 — Infrastructure must not import application:**
```java
noClasses().that().resideInAPackage("..infrastructure..")
    .and().doNotHaveSimpleName("CxfConfig")
    .should().dependOnClassesThat()
    .resideInAPackage("..application..");
```
`CxfConfig` is exempted — it must reference ResponderImpls to publish CXF endpoints.

**Rule 4 — Domain isolation:** Already exists (rules A in current `ArchitectureTest.java`).

**Rule 5 — Controllers/ResponderImpls must reside in `.api` packages:**
```java
classes().that().haveSimpleNameEndingWith("Controller")
    .and().resideInAPackage("..application..")
    .should().resideInAPackage("..api..");

classes().that().haveSimpleNameEndingWith("ResponderImpl")
    .should().resideInAPackage("..application..api..");
```

### Step 5 (optional): Refactor 5 service classes to use shared `JaxbXmlMarshaller`

Each application service (`RegisterCertificateService`, `RevokeCertificateService`, `SendMessageToRecipientService`, `CertificateStatusUpdateForCareService`, `StoreLogService`) has its own duplicated `JAXBContext` + `marshalToXml` private method. These can be replaced with a call to the shared `JaxbXmlMarshaller`. This reduces duplication but is not required for the architecture enforcement task — flagging for approval.

## Verification

1. `./gradlew spotlessApply` — format all moved/changed files
2. `./gradlew :app:test` — unit tests + ArchUnit rules pass
3. `./gradlew :integration-test:integrationTest` — SOAP endpoints still work
4. `./gradlew build :integration-test:integrationTest` — full build green
