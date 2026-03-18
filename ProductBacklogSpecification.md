# Product Backlog Specification — intyg-mock-service

This document captures suggested future development items for intyg-mock-service. Each item is intentionally scoped to a single module or concern so it can be picked up and implemented independently. The primary drivers are:

- **REST API usability** — making the inspection endpoints richer for both manual testers and automated test suites that need to assert on what was received.
- **SOAP passthrough** — allowing the mock to act as a transparent proxy, storing calls locally while also forwarding them to an upstream system.

---

## ~~REST API Parity — RevokeCertificate~~ ✓ Done

~~The three items below bring `RevokeCertificate` to the same level as `RegisterCertificate`.~~

### ~~RC-01 — Add GET and DELETE by certificate ID for RevokeCertificate~~ ✓ Done

~~Add `GET /api/revoke-certificate/{certificateId}` returning the matching DTO (404 if not found) and `DELETE /api/revoke-certificate/{certificateId}` to remove a single entry. Add `findByCertificateId` and `deleteById` to `RevokeCertificateRepository`. Add integration tests.~~

### ~~RC-02 — Add GET by logical address for RevokeCertificate~~ ✓ Done

~~Add `GET /api/revoke-certificate/logical-address/{logicalAddress}` returning a list of matching DTOs. Add `findByLogicalAddress` to `RevokeCertificateRepository` delegating to `findByKey`. Add integration test.~~

### ~~RC-03 — Add GET by person ID for RevokeCertificate~~ ✓ Done

~~Add `GET /api/revoke-certificate/person/{personId}` filtering by `patientPersonId.extension` with the same hyphen-normalisation used in `RegisterCertificate`. Add `findByPersonId` to `RevokeCertificateRepository`. Add integration test.~~

---

## ~~REST API Parity — SendMessageToRecipient~~ ✓ Done

~~The four items below bring `SendMessageToRecipient` to the same level as `RevokeCertificate`.~~

### ~~SM-01 — Add GET and DELETE by message ID for SendMessageToRecipient~~ ✓ Done

~~Add `GET /api/send-message-to-recipient/{messageId}` returning the matching DTO (404 if not found) and `DELETE /api/send-message-to-recipient/{messageId}` to remove a single message. Add `findByMessageId` and `deleteByMessageId` to `SendMessageToRecipientRepository`. Add integration tests.~~

### ~~SM-02 — Add GET by certificate ID for SendMessageToRecipient~~ ✓ Done

~~Add `GET /api/send-message-to-recipient/certificate/{certificateId}` filtering by `intygsId.extension`. Add `findByCertificateId` to the repository. Add integration test.~~

### ~~SM-03 — Add GET by person ID for SendMessageToRecipient~~ ✓ Done

~~Add `GET /api/send-message-to-recipient/person/{personId}` filtering by `patientPersonId.extension` with hyphen normalisation. Add `findByPersonId` to the repository. Add integration test.~~

### ~~SM-04 — Add GET by logical address for SendMessageToRecipient~~ ✓ Done

~~Add `GET /api/send-message-to-recipient/logical-address/{logicalAddress}` returning messages stored under that logical address. Add `findByLogicalAddress` to the repository. Add integration test.~~

---

## ~~REST API Parity — CertificateStatusUpdateForCare~~ ✓ Done

~~The four items below bring `CertificateStatusUpdateForCare` to the same level as `RevokeCertificate` and `SendMessageToRecipient`.~~

### ~~CS-01 — Add GET and DELETE by certificate ID for CertificateStatusUpdateForCare~~ ✓ Done

~~Add `GET /api/certificate-status-for-care/{certificateId}` returning matching DTOs and `DELETE /api/certificate-status-for-care/{certificateId}` to remove entries for a certificate. Add `findByCertificateId` and `deleteByCertificateId` to `CertificateStatusUpdateForCareRepository`. Add integration tests.~~

### ~~CS-02 — Add GET by logical address for CertificateStatusUpdateForCare~~ ✓ Done

~~Add `GET /api/certificate-status-for-care/logical-address/{logicalAddress}`. Add `findByLogicalAddress` to the repository. Add integration test.~~

### ~~CS-03 — Add GET by person ID for CertificateStatusUpdateForCare~~ ✓ Done

~~Add `GET /api/certificate-status-for-care/person/{personId}` filtering by `intyg.patient.personId.extension` with hyphen normalisation. Add `findByPersonId` to the repository. Add integration test.~~

### ~~CS-04 — Add GET by event type for CertificateStatusUpdateForCare~~ ✓ Done

~~Add `GET /api/certificate-status-for-care/event-type/{eventCode}` filtering by `handelse.handelsekod.code`. Useful for asserting that a specific status event (e.g. `SKICKA`, `MAKULERA`) was received. Add `findByEventCode` to the repository. Add integration test.~~

---

## Cross-Cutting REST Enhancements

### ~~XC-01 — Add count endpoint for each module~~ ✓ Done

~~Add `GET /api/{module}/count` returning `{"count": N}` for each of the five modules. Allows automated tests to assert "exactly N calls were received" without fetching all records.~~

~~Endpoints:~~
~~- `GET /api/register-certificate/count`~~
~~- `GET /api/revoke-certificate/count`~~
~~- `GET /api/send-message-to-recipient/count`~~
~~- `GET /api/certificate-status-for-care/count`~~
~~- `GET /api/store-log/count`~~

### ~~XC-02 — Add global reset endpoint~~ ✓ Done

~~Add `DELETE /api/reset` that calls `deleteAll()` on all five repositories in a single request. Useful as a `@BeforeEach` setup call in automated integration test suites that run against a shared instance.~~

---

## SOAP Passthrough

The passthrough feature lets the mock store each incoming SOAP call locally (existing behaviour) and simultaneously forward it to a real upstream system. This is useful when running integration tests against a full environment where the real service must also receive the call.

Each module is a separate item. **PT-00 is a prerequisite for PT-01 through PT-05.**

### PT-00 — Add common CXF client infrastructure for passthrough

Add `cxf-rt-frontend-jaxws` to `app/build.gradle`. Introduce per-service configuration properties:

```
app.passthrough.register-certificate.enabled=false
app.passthrough.register-certificate.url=
app.passthrough.revoke-certificate.enabled=false
app.passthrough.revoke-certificate.url=
# … etc.
```

Create a reusable CXF JAXWS client factory helper (or configure in `CxfConfig`). Document the configuration pattern in `application.properties`. No functional change to existing behaviour when all flags are `false`.

### PT-01 — Passthrough for RegisterCertificate

When `app.passthrough.register-certificate.enabled=true`, `RegisterCertificateResponderImpl` forwards the call to the URL at `app.passthrough.register-certificate.url` using a CXF JAXWS client after storing the request locally. The upstream response is logged but the local mock always returns `OK` to the caller. Add integration test using WireMock or a local stub.

### PT-02 — Passthrough for RevokeCertificate

Same pattern as PT-01 for the RevokeCertificate service. Configuration key: `app.passthrough.revoke-certificate`.

### PT-03 — Passthrough for SendMessageToRecipient

Same pattern as PT-01 for the SendMessageToRecipient service. Configuration key: `app.passthrough.send-message-to-recipient`.

### PT-04 — Passthrough for CertificateStatusUpdateForCare

Same pattern as PT-01 for the CertificateStatusUpdateForCare service. Configuration key: `app.passthrough.certificate-status-update-for-care`.

### PT-05 — Passthrough for StoreLog

Same pattern as PT-01 for the StoreLog service. Configuration key: `app.passthrough.store-log`.
