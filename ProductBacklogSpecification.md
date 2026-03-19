# Product Backlog Specification — intyg-mock-service

This document captures suggested future development items for intyg-mock-service. Each item is intentionally scoped to a single module or concern so it can be picked up and implemented independently.

---

## Previously Implemented

- REST API parity for all five services (GET/DELETE by ID, logical address, person ID, etc.)
- Cross-cutting: count endpoints (`GET /api/{module}/count`) and global reset (`DELETE /api/reset`)
- SOAP passthrough for all five services with mTLS support

---

## Mock Behavior Control

A centralized REST API at `/api/behavior` for creating runtime rules that control how each SOAP service responds. Rules are evaluated on every incoming SOAP call. Error rules prevent the request from being stored (it was rejected); delay-only rules store normally. Skipped stores are logged via a generic `BehaviorLogger`.

A rule can combine delay AND error response in a single rule (e.g., `delayMillis=30000` + `resultCode=ERROR` + `errorId=VALIDATION_ERROR` → wait 30s then return error). Delay-only or error-only also supported.

### ~~MB-01 — Error response rules for RegisterCertificate (full vertical slice)~~ ✓ Done

~~First item — establishes the entire pattern that subsequent items follow.~~

Deliver:
- `BehaviorRule` model (`@Value @Builder`): `id` (UUID), `serviceName` (enum), `resultCode` (nullable), `errorId` (nullable), `resultText` (nullable), `delayMillis` (Long, nullable), `matchCriteria` (logicalAddress / certificateId / personId — all nullable), `maxTriggerCount` (Integer, nullable — auto-remove after N fires), `triggerCount`, `createdAt`
- `BehaviorRuleRepository` — `ConcurrentHashMap<UUID, BehaviorRule>` with CRUD + `findByServiceName` + trigger-count tracking + auto-removal
- `BehaviorRuleEvaluator` — matches rules by specificity (more non-null criteria = higher priority, ties broken by most-recent `createdAt`), handles delay via injectable `DelayApplier` (`Thread.sleep` in prod)
- `BehaviorController` — full REST API at `/api/behavior`:
  - `POST /api/behavior` — create rule
  - `GET /api/behavior` — list all (optionally `?service=register-certificate`)
  - `GET /api/behavior/{ruleId}` — get specific rule
  - `DELETE /api/behavior/{ruleId}` — delete specific rule
  - `DELETE /api/behavior` — delete all (optionally `?service=...`)
- Wire into `ResetController` so `DELETE /api/reset` clears behavior rules
- `BehaviorLogger` — generic structured logger for behavior rule outcomes, used by all services. Logs service name, matched rule ID, certificate/person ID, and outcome (e.g., `"RegisterCertificate with certificateId 'abc-123' not stored due to simulated VALIDATION_ERROR behavior (rule 550e8400-...)"`). Uses the existing `log.atInfo().setMessage(...).addKeyValue(...).log()` pattern
- Wire into `RegisterCertificateService.store()`: (1) extract match context (logicalAddress, certificateId, personId), (2) evaluate rule, (3) if error rule matches → apply delay if present, log via `BehaviorLogger`, return error response **without storing**, (4) if delay-only rule matches → apply delay, store request, return OK, (5) no rule → fall through to passthrough/OK and store as before
- Unit tests for: `BehaviorRuleRepository`, `BehaviorRuleEvaluator` (matching, specificity, trigger-count), `BehaviorLogger`, `RegisterCertificateService` (error rule skips store, delay-only rule stores, no rule stores)
- Integration tests: create error rule via REST → send SOAP → verify error response + request **not** stored; `maxTriggerCount=1` fires once then OK; reset clears rules; delay-only rule stores normally
- OpenAPI annotations on all endpoints

Example POST:
```json
{
  "serviceName": "REGISTER_CERTIFICATE",
  "resultCode": "ERROR",
  "errorId": "VALIDATION_ERROR",
  "resultText": "Certificate validation failed",
  "matchCriteria": { "certificateId": "abc-123" },
  "maxTriggerCount": 1
}
```

Prerequisites: none

### MB-02 — Error response rules for RevokeCertificate

Reuse all infrastructure from MB-01. Only new work:
- Wire `BehaviorRuleEvaluator` into `RevokeCertificateService.store()`
- Extract match context from `RevokeCertificateType` (logicalAddress, `intygsId.extension`, `patientPersonId.extension`)
- Reuse the shared `CertificateBehaviorResponseBuilder` (same `certificate.v3.ResultType`)
- Unit tests for `RevokeCertificateService` rule evaluation
- Integration test: error rule → SOAP → verify error response + request stored

Prerequisites: MB-01

### MB-03 — Error response rules for SendMessageToRecipient

Same pattern as MB-02:
- Wire into `SendMessageToRecipientService.store()`
- Extract match context from `SendMessageToRecipientType` (logicalAddress, `intygsId.extension`, `patientPersonId.extension`)
- Unit tests + integration test

Prerequisites: MB-01

### MB-04 — Error response rules for CertificateStatusUpdateForCare

Same pattern as MB-02:
- Wire into `CertificateStatusUpdateForCareService.store()`
- Extract match context from `CertificateStatusUpdateForCareType` (logicalAddress, `intyg.intygsId.extension`, `intyg.patient.personId.extension`)
- Unit tests + integration test

Prerequisites: MB-01

### MB-05 — Error response rules for StoreLog

Different schema — requires `StoreLogBehaviorResponseBuilder`:
- Wire into `StoreLogService.store()`
- Uses `auditing.log.v2.ResultType` (no `errorId` field, different `ResultCodeType` enum with VALIDATION_ERROR, ACCESSDENIED, etc.)
- Extract match context: `logicalAddress` + certificate ID from `log.activity.activityLevel`
- Separate response builder that maps rule's `resultCode` string to `auditing.log.v2.ResultCodeType`
- Unit tests + integration test

Prerequisites: MB-01

### Dependency Graph

```
MB-01 (RegisterCertificate — full infra + first service)
  ├── MB-02 (RevokeCertificate)
  ├── MB-03 (SendMessageToRecipient)
  ├── MB-04 (CertificateStatusUpdateForCare)
  └── MB-05 (StoreLog — different schema)
```

MB-02 through MB-05 are independent of each other and can be done in any order after MB-01.

### Key Design Decisions

1. **Centralized `/api/behavior` endpoint** — one place to manage all rules, `serviceName` field provides scoping
2. **Combined delay + error** — a single rule can have both `delayMillis` and error fields; delay-only or error-only also work
3. **Rules evaluated in service layer** — response types differ per schema, keeps logic explicit and follows existing patterns
4. **Behavior rules override passthrough** — most useful for testing (override upstream for specific scenarios)
5. **Don't store on error** — if a rule produces an error response, the request is **not** stored (it was rejected). Delay-only rules still store normally. The skipped store is logged via a generic `BehaviorLogger` (see below)
6. **Thread-safe `ConcurrentHashMap`** — rules managed via REST while SOAP requests processed concurrently
7. **Specificity-based matching** — more specific rules win (certificateId+logicalAddress beats logicalAddress-only)

### Schema Reference

**Certificate v3** (RegisterCertificate, RevokeCertificate, SendMessageToRecipient, CertificateStatusUpdateForCare):
- `ResultType`: `resultCode` (OK | INFO | ERROR) + `errorId` (VALIDATION_ERROR | APPLICATION_ERROR | TECHNICAL_ERROR | REVOKED) + `resultText` (String)

**StoreLog v2**:
- `ResultType`: `resultCode` (OK | INFO | ERROR | VALIDATION_ERROR | ACCESSDENIED | ...) + `resultText` (String) — **no `errorId` field**
