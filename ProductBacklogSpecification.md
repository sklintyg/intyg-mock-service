# Product Backlog Specification — intyg-mock-service

This document captures the planned development items for intyg-mock-service. Items are horizontally
sliced so each can be implemented and verified independently.

---

## Previously Implemented

### Mock Behavior Control (`/api/behavior`)

A centralized REST API for creating runtime rules that control how each SOAP service responds.
Rules combine delay and/or error response, are evaluated per incoming SOAP call, and selected by
specificity-based matching (certificateId + logicalAddress + personId). All five services wired:
RegisterCertificate, RevokeCertificate, SendMessageToRecipient, CertificateStatusUpdateForCare,
StoreLog.

### REST Inspection API (`/api/`)

Per-service GET/DELETE endpoints exposing stored SOAP data as DTOs. Cross-cutting count endpoints
and global reset (`DELETE /api/reset`). SOAP passthrough with mTLS for all five services.

---

## Navigation API — HATEOAS domain-centric REST (`/api/navigate/`)

A read-only REST API that exposes a rich **domain model** assembled from the five existing
in-memory repositories. The model reflects clinical concepts (Certificate, Message, Revocation,
StatusUpdate, LogEntry) and organizational entities (Patient, Unit, Staff). Navigation links
(HATEOAS / HAL format via Spring HATEOAS) allow clients to traverse mock data without prior
knowledge of the internal structure.

### Architecture

```
domain/navigation/model/          ← Certificate, Patient, Staff, Unit, CareProvider,
                                     Message, Revocation, StatusUpdate, LogEntry
domain/navigation/repository/     ← Query repository interfaces (domain ports)
infrastructure/repository/navigation/  ← Implementations federating existing repos
application/navigation/           ← Controllers, Services, Assemblers, Response records
```

Assemblers use `WebMvcLinkBuilder.linkTo(methodOn(...))` for typed, controller-method-based links.
Controllers delegate to `*NavigationService` beans (never directly to domain repositories).

### Domain Model

| Entity | Key Fields | Primary Source |
|---|---|---|
| `Certificate` | `certificateId`, `certificateType?`, `signingTimestamp?`, `sentTimestamp?`, `version?`, `logicalAddress?`, `patient?`, `issuedBy?` | RegisterCertificate (merged from all 5 repos) |
| `Patient` | `personId` (normalised), `firstName?`, `lastName?`, `streetAddress?`, `postalCode?`, `city?` | PatientDTO |
| `Staff` | `staffId`, `fullName?`, `prescriptionCode?`, `unit?` | HoSPersonalDTO |
| `Unit` | `unitId`, `unitName?`, `streetAddress?`, `postalCode?`, `city?`, `phone?`, `email?`, `careProvider?` | EnhetDTO |
| `CareProvider` | `careProviderId`, `careProviderName?` | VardgivareDTO |
| `Message` | `messageId`, `certificateId`, `recipient`, `subject?`, `heading?`, `body?`, `sentTimestamp`, `personId`, `sentBy?` | SendMessageToRecipient |
| `Revocation` | `certificateId`, `personId`, `revokedAt`, `reason?`, `revokedBy?` | RevokeCertificate |
| `StatusUpdate` | `certificateId`, `personId`, `eventCode`, `eventDisplayName?`, `eventTimestamp`, `questionsSentTotal`, `questionsReceivedTotal`, `logicalAddress?` | CertificateStatusUpdateForCare |
| `LogEntry` | `logId`, `systemId?`, `systemName?`, `activityType?`, `certificateId` (=activityLevel), `purpose?`, `activityStart?`, `userId?`, `userAssignment?`, `careUnitId?`, `careProviderName?` | StoreLog |

Certificate list is a **merged view**: all unique certificate IDs from all five services.
RegisterCertificate provides full data; other services contribute certificateId + personId stubs
for IDs not seen via registration.

Patient IDs in URLs are **normalised** (hyphens removed).

### HATEOAS Links Per Entity

| Entity | Links |
|---|---|
| `CertificateResponse` | `self`, `patient?`, `unit?`, `issuer?`, `revocation`, `messages`, `status-updates`, `log-entries` |
| `PatientResponse` | `self`, `certificates`, `messages` |
| `MessageResponse` | `self`, `certificate`, `patient` |
| `RevocationResponse` | `self`, `certificate`, `patient` |
| `StatusUpdateResponse` | `self`, `certificate`, `patient` |
| `LogEntryResponse` | `self`, `certificate?` |
| `UnitResponse` | `self`, `certificates` |
| `StaffResponse` | `self`, `certificates` |

---

### ~~NAV-01 — Foundation + Certificate resource~~ ✓ Done

Spring HATEOAS added. Domain model (`Certificate`, `Patient`, `Staff`, `Unit`, `CareProvider`)
created. `CertificateNavigationRepositoryImpl` federates all five existing repositories.
`CertificateController` at `/api/navigate/certificates` with full HAL links. Unit tests and
integration tests verified.

Endpoints delivered:
- `GET /api/navigate/certificates`
- `GET /api/navigate/certificates/{certificateId}`
- `GET /api/navigate/certificates/{id}/messages` (stub — returns empty)
- `GET /api/navigate/certificates/{id}/status-updates` (stub — returns empty)
- `GET /api/navigate/certificates/{id}/log-entries` (stub — returns empty)
- `GET /api/navigate/certificates/{id}/revocation` (stub — returns 404)

springdoc-openapi upgraded to 2.8.16 for Spring Boot 3.5.x + HATEOAS compatibility.

Prerequisites: none

---

### ~~NAV-02 — Patient resource~~ ✓ Done

Deliver patient endpoint and patient sub-collection of certificates.

**Domain model:** `Patient` already exists from NAV-01.

**New work:**
- `PatientNavigationRepository` interface — `findByPersonId(String normalizedPersonId)` returning
  `Optional<Patient>` (assembled from first RegisterCertificate match; falls back to partial data
  from other repos)
- `PatientNavigationRepositoryImpl` in `infrastructure/repository/navigation/`
- `PatientNavigationService` (`@Service`) in `application/navigation/patient/`
- `PatientResponse` (record), `PatientAssembler`, `PatientController`
- Endpoints:
  - `GET /api/navigate/patients/{personId}`
  - `GET /api/navigate/patients/{personId}/certificates`
- Fill in `GET /api/navigate/patients/{personId}/messages` stub (returns empty for now)
- Unit tests: `PatientNavigationRepositoryImplTest`, `PatientAssemblerTest`,
  `PatientNavigationServiceTest`, `PatientControllerTest`
- Integration test: post SOAP → GET `/api/navigate/patients/{personId}` → assert HAL with
  `certificates` and `patient` links

Prerequisites: NAV-01

---

### ~~NAV-03 — Message resource~~ ✓ Done

Deliver message list, detail, and cross-references.

**New work:**
- Domain model: `Message`
- `MessageNavigationRepository` interface — `findAll()`, `findById(String)`,
  `findByCertificateId(String)`, `findByPersonId(String normalizedPersonId)`
- `MessageNavigationRepositoryImpl` using `SendMessageToRecipientRepository` + existing
  `SendMessageToRecipientConverter`
- `MessageNavigationService`, `MessageResponse`, `MessageAssembler`, `MessageController`
- Endpoints:
  - `GET /api/navigate/messages`
  - `GET /api/navigate/messages/{messageId}`
- Fill in `GET /api/navigate/certificates/{id}/messages` (replaces NAV-01 stub)
- Fill in `GET /api/navigate/patients/{personId}/messages` (replaces NAV-02 stub)
- Unit tests + integration test

Prerequisites: NAV-01

---

### ~~NAV-04 — Revocation resource~~ ✓ Done

Deliver revocation as certificate sub-resource.

**New work:**
- Domain model: `Revocation`
- `RevocationNavigationRepository` — `findByCertificateId(String)`, `findByPersonId(String)`
- `RevocationNavigationRepositoryImpl` using `RevokeCertificateRepository`
- `RevocationNavigationService`, `RevocationResponse`, `RevocationAssembler`,
  `RevocationController`
- Fill in `GET /api/navigate/certificates/{id}/revocation` (replaces NAV-01 stub)
- Unit tests + integration test

Prerequisites: NAV-01

---

### NAV-05 — StatusUpdate resource

Deliver status update list and certificate sub-resource.

**New work:**
- Domain model: `StatusUpdate`
- `StatusUpdateNavigationRepository` — `findAll()`, `findByCertificateId(String)`,
  `findByPersonId(String)`
- `StatusUpdateNavigationRepositoryImpl` using `CertificateStatusUpdateForCareRepository`
- `StatusUpdateNavigationService`, `StatusUpdateResponse`, `StatusUpdateAssembler`,
  `StatusUpdateController`
- Endpoints:
  - `GET /api/navigate/status-updates`
- Fill in `GET /api/navigate/certificates/{id}/status-updates` (replaces NAV-01 stub)
- Unit tests + integration test

Prerequisites: NAV-01

---

### NAV-06 — LogEntry resource

Deliver log entry list and certificate sub-resource.

**New work:**
- Domain model: `LogEntry`
- `LogEntryNavigationRepository` — `findAll()`, `findByCertificateId(String)`
- `LogEntryNavigationRepositoryImpl` using `StoreLogTypeRepository` + reuse
  `StoreLogTypeConverter`
- `LogEntryNavigationService`, `LogEntryResponse`, `LogEntryAssembler`, `LogEntryController`
- Endpoints:
  - `GET /api/navigate/log-entries`
- Fill in `GET /api/navigate/certificates/{id}/log-entries` (replaces NAV-01 stub)
- Unit tests + integration test

Prerequisites: NAV-01

---

### NAV-07 — Unit resource

Deliver care units as top-level navigable resources.

**New work:**
- `UnitNavigationRepository` — `findAll()` (collects all unique units from RegisterCertificate
  data), `findById(String unitId)`, `findCertificatesByUnitId(String)`
- `UnitNavigationRepositoryImpl`
- `UnitNavigationService`, `UnitResponse`, `UnitAssembler`, `UnitController`
- Endpoints:
  - `GET /api/navigate/units`
  - `GET /api/navigate/units/{unitId}`
  - `GET /api/navigate/units/{unitId}/certificates`
- Unit tests + integration test

Prerequisites: NAV-01

---

### NAV-08 — Staff resource

Deliver staff (health professionals) as top-level navigable resources.

**New work:**
- `StaffNavigationRepository` — `findAll()` (collects all unique staff from RegisterCertificate
  data), `findById(String staffId)`, `findCertificatesByStaffId(String)`
- `StaffNavigationRepositoryImpl`
- `StaffNavigationService`, `StaffResponse`, `StaffAssembler`, `StaffController`
- Endpoints:
  - `GET /api/navigate/staff`
  - `GET /api/navigate/staff/{staffId}`
  - `GET /api/navigate/staff/{staffId}/certificates`
- Unit tests + integration test

Prerequisites: NAV-01

---

### Dependency Graph

```
NAV-01 (Foundation + Certificate — merged view, full HATEOAS infra)  ✓ Done
  ├── NAV-02 (Patient)
  ├── NAV-03 (Message)
  ├── NAV-04 (Revocation)
  ├── NAV-05 (StatusUpdate)
  ├── NAV-06 (LogEntry)
  ├── NAV-07 (Unit)
  └── NAV-08 (Staff)
```

NAV-02 through NAV-08 are independent of each other and can be implemented in any order after
NAV-01. Each fills in stub endpoints from earlier slices as it goes.
