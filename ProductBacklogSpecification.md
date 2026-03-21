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

~~Deliver:~~

~~**Domain model** (`se.inera.intyg.intygmockservice.domain`):~~
- ~~`BehaviorRule` — `@Getter @Builder(toBuilder = true)` rich entity. Fields: `id` (UUID), `serviceName` (ServiceName), `resultCode` (String, nullable), `errorId` (String, nullable), `resultText` (String, nullable), `delayMillis` (Long, nullable), `matchCriteria` (MatchCriteria, nullable), `maxTriggerCount` (Integer, nullable), `triggerCount` (mutable int), `createdAt` (Instant). Runtime-injected fields: `delayApplier`, `eventLogger`, `onExhausted`. Methods: `wire(DelayApplier, BehaviorEventLogger, Runnable onExhausted)`, `matches(MatchContext)`, `specificity()`, `evaluate(MatchContext)` → `Optional<EvaluationResult>`, `trigger()`, `isExhausted()`, `hasErrorEffect()`, `hasDelay()`~~
- ~~`MatchCriteria` — `@Value @Builder` with `logicalAddress`, `certificateId`, `personId` (all nullable). Methods: `matches(MatchContext)`, `specificity()` (count of non-null fields). Person ID matching strips hyphens before comparing.~~
- ~~`MatchContext` — `@Value @Builder` with `logicalAddress`, `certificateId`, `personId` (extracted from SOAP request)~~
- ~~`EvaluationResult` — `@Value @Builder` with `resultCode`, `errorId`, `resultText`~~
- ~~`ServiceName` — enum: `REGISTER_CERTIFICATE`, `REVOKE_CERTIFICATE`, `SEND_MESSAGE_TO_RECIPIENT`, `CERTIFICATE_STATUS_UPDATE_FOR_CARE`, `STORE_LOG`~~
- ~~`DelayApplier` — interface: `void apply(long millis)`~~
- ~~`BehaviorEventLogger` — interface: `logDelayApplied(ServiceName, String certificateId, BehaviorRule)` and `logErrorSkipped(ServiceName, String certificateId, BehaviorRule)`~~

~~Note: No `BehaviorRuleEvaluator` class — evaluation logic lives in `BehaviorRule.evaluate()`, matching and specificity-based selection in `BehaviorRuleRepository.findBestMatch()`.~~

~~**Infrastructure**:~~
- ~~`BehaviorRuleRepository` (`@Repository`) — `ConcurrentHashMap<UUID, BehaviorRule>` with `save`, `findById`, `findAll`, `findByServiceName`, `findBestMatch(ServiceName, MatchContext)`, `delete`, `deleteAll`, `deleteByServiceName`. `findBestMatch` filters by service and context, picks highest specificity (tiebreak: most recent `createdAt`), then **wires** the rule with `delayApplier`, `eventLogger`, and auto-delete callback before returning.~~
- ~~`BehaviorLogger` (`@Component`) — implements `BehaviorEventLogger`, structured ECS logging via `log.atInfo()`. Logs delay and error-skipped events with rule ID, result code, and certificate ID.~~
- ~~`ThreadSleepDelayApplier` (`@Component`) — implements `DelayApplier` via `Thread.sleep`. Handles `InterruptedException` by restoring interrupt flag and logging a warning.~~

~~**Application** (`application/behavior/`):~~
- ~~`BehaviorController` (`@RestController`) — full REST API at `/api/behavior`: `POST`, `GET` (all, optionally `?service=`), `GET /{ruleId}`, `DELETE /{ruleId}`, `DELETE` (all, optionally `?service=`). OpenAPI annotations on all endpoints.~~
- ~~`BehaviorService` (`@Service`) — CRUD operations, converts domain ↔ DTO~~
- ~~`CreateBehaviorRuleRequest` — Java record (input DTO) with inner `MatchCriteriaRequest` record~~
- ~~`BehaviorRuleDTO` — `@Value @Builder` (output DTO) with inner `MatchCriteriaDTO`~~

~~Wire into `ResetController` so `DELETE /api/reset` clears behavior rules.~~

~~**Service integration**:~~
- ~~`RegisterCertificateResponseFactory` (`@Component`) — `create(EvaluationResult)` → `RegisterCertificateResponseType`. Maps `resultCode` → `ResultCodeType`, `errorId` → `ErrorIdType`, `resultText` → string.~~
- ~~In `RegisterCertificateService.store()`: convert SOAP type → DTO first, build `MatchContext` from DTO fields, call `behaviorRuleRepository.findBestMatch(REGISTER_CERTIFICATE, context)`, if present call `rule.evaluate(context)`, if result present return error response without storing. Delay-only rules (no `resultCode`) cause `evaluate()` to return `Optional.empty()` → store proceeds normally.~~

~~**Unit tests** (one class per production class, no Spring context): `BehaviorRuleTest` (evaluate, trigger, exhaustion, delay), `MatchCriteriaTest` (matching, specificity, person ID normalisation), `BehaviorRuleRepositoryTest` (CRUD, findBestMatch specificity/tiebreak, wiring, auto-delete), `BehaviorLoggerTest`, `BehaviorServiceTest`, `RegisterCertificateResponseFactoryTest`, `RegisterCertificateServiceTest` (error rule skips store, delay-only rule stores, no rule stores).~~

~~**Integration tests**: create error rule via REST → send SOAP → verify error response + request **not** stored; `maxTriggerCount=1` fires once then auto-removed; reset clears rules; delay-only rule stores normally.~~

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

### ~~MB-02 — Error response rules for RevokeCertificate~~ ✓ Done

Reuse all infrastructure from MB-01. Only new work:
- Add `RevokeCertificateResponseFactory` (`@Component`) — `create(EvaluationResult)` → `RevokeCertificateResponseType`. Same mapping logic as `RegisterCertificateResponseFactory` but wraps `certificate.v3.ResultType` in the Revoke-specific response type.
- Wire `behaviorRuleRepository.findBestMatch(REVOKE_CERTIFICATE, context)` + `rule.evaluate(context)` into `RevokeCertificateService.store()`
- Extract match context from `RevokeCertificateType`: logicalAddress, `intygsId.extension`, `patientPersonId.extension`
- Unit tests for `RevokeCertificateService` (error rule skips store, delay-only rule stores) and `RevokeCertificateResponseFactory`
- Integration test: error rule → SOAP → verify error response + request **not** stored

Prerequisites: MB-01

### MB-03 — Error response rules for SendMessageToRecipient

Same pattern as MB-02:
- Add `SendMessageToRecipientResponseFactory` (`@Component`) — wraps `certificate.v3.ResultType` in `SendMessageToRecipientResponseType`
- Wire into `SendMessageToRecipientService.store()` with `ServiceName.SEND_MESSAGE_TO_RECIPIENT`
- Extract match context from `SendMessageToRecipientType`: logicalAddress, `intygsId.extension`, `patientPersonId.extension`
- Unit tests for service and response factory + integration test

Prerequisites: MB-01

### MB-04 — Error response rules for CertificateStatusUpdateForCare

Same pattern as MB-02:
- Add `CertificateStatusUpdateForCareResponseFactory` (`@Component`) — wraps `certificate.v3.ResultType` in `CertificateStatusUpdateForCareResponseType`
- Wire into `CertificateStatusUpdateForCareService.store()` with `ServiceName.CERTIFICATE_STATUS_UPDATE_FOR_CARE`
- Extract match context from `CertificateStatusUpdateForCareType`: logicalAddress, `intyg.intygsId.extension`, `intyg.patient.personId.extension`
- Unit tests for service and response factory + integration test

Prerequisites: MB-01

### MB-05 — Error response rules for StoreLog

Different schema — requires a separate `StoreLogResponseFactory`:
- Add `StoreLogResponseFactory` (`@Component`) — `create(EvaluationResult)` → `StoreLogResponseType`. Uses `auditing.log.v2.ResultType` which has **no `errorId` field** and a different `ResultCodeType` enum (VALIDATION_ERROR, ACCESSDENIED, etc.). Maps rule's `resultCode` string to `auditing.log.v2.ResultCodeType`; ignore `errorId` from `EvaluationResult`.
- Wire `behaviorRuleRepository.findBestMatch(STORE_LOG, context)` + `rule.evaluate(context)` into `StoreLogService.store()`
- Extract match context: `logicalAddress` + certificate ID from `log.activity.activityLevel` (no personId for StoreLog)
- Unit tests for service and response factory + integration test

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
3. **Rules evaluated in domain entity** — `BehaviorRule.evaluate()` contains evaluation logic (apply delay, trigger count, return `EvaluationResult`); `BehaviorRuleRepository.findBestMatch()` handles specificity-based selection and wires dependencies into the rule before returning it. Each service has its own `*ResponseFactory` because the JAXB response wrapper type differs per service.
4. **Behavior rules override passthrough** — most useful for testing (override upstream for specific scenarios)
5. **Don't store on error** — if a rule produces an error response, the request is **not** stored (it was rejected). Delay-only rules still store normally. The skipped store is logged via a generic `BehaviorLogger` (see below)
6. **Thread-safe `ConcurrentHashMap`** — rules managed via REST while SOAP requests processed concurrently
7. **Specificity-based matching** — more specific rules win (certificateId+logicalAddress beats logicalAddress-only)
8. **Domain interfaces for testability** — `DelayApplier` and `BehaviorEventLogger` are domain interfaces injected at runtime via `BehaviorRule.wire()`. Unit tests can pass no-op or spy implementations without mocking infrastructure beans.

### Schema Reference

**Certificate v3** (RegisterCertificate, RevokeCertificate, SendMessageToRecipient, CertificateStatusUpdateForCare):
- `ResultType`: `resultCode` (OK | INFO | ERROR) + `errorId` (VALIDATION_ERROR | APPLICATION_ERROR | TECHNICAL_ERROR | REVOKED) + `resultText` (String)

**StoreLog v2**:
- `ResultType`: `resultCode` (OK | INFO | ERROR | VALIDATION_ERROR | ACCESSDENIED | ...) + `resultText` (String) — **no `errorId` field**
