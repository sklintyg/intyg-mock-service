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

### Navigation API — HATEOAS domain-centric REST (`/api/navigate/`)

A read-only REST API exposing a rich domain model assembled from the five in-memory repositories.
Full HATEOAS/HAL navigation across Certificate, Patient, Staff, Unit, Message, Revocation,
StatusUpdate, LogEntry. Delivered in slices NAV-01 through NAV-08 (all ✓ Done).

---

## DDD Remediation

Items below address findings from the DDD domain model assessment. Each item is independently
plannable, implementable, and verifiable. Findings are sourced from the assessment report dated
2026-03-26.

### Verification checklist (applies to every item)

Every item must pass all four gates before it is considered done:

1. `./gradlew :app:test` — unit tests green
2. `./gradlew spotlessCheck` — formatting clean
3. `./gradlew :integration-test:integrationTest` — integration tests green
4. ArchUnit rules in `ArchitectureTest` pass (included in gate 1 — ArchUnit runs as a unit test)

Where an item adds new ArchUnit rules, those are stated explicitly. Where it changes existing
tests, the change and reason are stated explicitly.

---

### ~~DDD-01 — BehaviorRule construction invariant~~ ✓ Done

**Finding:** A-2 (High) — `BehaviorRule` can be built with `errorId` set but `resultCode` null.
`hasErrorEffect()` then returns true and `evaluate()` produces an `EvaluationResult` with a null
`resultCode`, silently violating the invariant that an error-producing rule must carry a result
code. The aggregate root must refuse incoherent construction.

**New work:**

- Add a static factory method `BehaviorRule.create(...)` (or use a custom Lombok builder `build()`
  override) that throws `IllegalArgumentException` if `errorId != null && resultCode == null`.
- Make the Lombok-generated builder `build()` private or redirect through the factory so the
  invariant cannot be bypassed.

**Test changes:**

- `BehaviorRuleTest` — add test: constructing a rule with `errorId` set and no `resultCode` throws
  `IllegalArgumentException`. **Reason:** new invariant, previously unguarded path.
- `BehaviorRuleEvaluationTest` — add test: constructing a valid error-producing rule (both
  `resultCode` and `errorId` non-null) succeeds. **Reason:** positive counterpart to the new
  guard, confirms the valid path still works.
- No existing tests are broken or removed.

**ArchUnit changes:** none — this is a behavioural invariant, not a structural dependency rule.

Prerequisites: none

---

### ~~DDD-02 — Decouple BehaviorRule from infrastructure collaborators~~ ✓ Done

**Finding:** A-1 (High) + A-3 (Medium) + L-1 (Medium) — `BehaviorRule` holds `DelayApplier`,
`BehaviorEventLogger`, and `Runnable onExhausted` as fields wired post-construction via `wire()`.
The aggregate calls these collaborators directly inside `evaluate()`. Domain aggregates must not
call infrastructure-layer services; they must return a result and let the application layer act on
it. The `wire()` method is infrastructure language with no domain meaning, and allows silent
re-wiring that makes aggregate state unpredictable.

**New work:**

- Introduce a new value type `RuleEvaluation` in `domain/behavior/model/`:
  ```
  record RuleEvaluation(
      Optional<EvaluationResult> errorResult,
      boolean delayRequested,
      boolean exhausted
  )
  ```
- Remove fields `delayApplier`, `eventLogger`, `onExhausted` from `BehaviorRule`.
- Remove method `wire(DelayApplier, BehaviorEventLogger, Runnable)` from `BehaviorRule`.
- Change `evaluate(MatchContext)` to return `Optional<RuleEvaluation>` instead of
  `Optional<EvaluationResult>`. The method no longer calls `DelayApplier` or `BehaviorEventLogger`
  — it records intent in the returned value.
- Update the application-layer service that currently calls `rule.wire(...)` and
  `rule.evaluate(...)` to: inspect `RuleEvaluation.delayRequested()` and call `DelayApplier`
  itself; call `BehaviorEventLogger` itself; handle `RuleEvaluation.exhausted()` itself.
- Delete `DelayApplier` and `BehaviorEventLogger` from `domain/behavior/service/` if they have no
  remaining domain callers (move them to the application layer, or delete them entirely if the
  application layer can own equivalent interfaces).

**Test changes:**

- `BehaviorRuleTest` — remove all tests that call `wire()` or pass mock `DelayApplier` /
  `BehaviorEventLogger` to the rule. **Reason:** those collaborators no longer exist on the
  aggregate.
- `BehaviorRuleTest` — add tests for the new `evaluate()` return shape: assert that
  `RuleEvaluation.delayRequested()` is true when `delayMillis` is set; assert
  `RuleEvaluation.exhausted()` when `maxTriggerCount` is reached; assert
  `RuleEvaluation.errorResult()` is present when `resultCode` is set.
  **Reason:** new return contract replaces the old evaluate-and-side-effect model.
- `BehaviorRuleEvaluationTest` — rewrite entirely. The current tests assert that `DelayApplier`
  and `BehaviorEventLogger` are called by the rule and that `wire()` wires them correctly.
  After this change those are application-layer concerns. New tests assert only that
  `evaluate()` produces the correct `RuleEvaluation` values for given rule configurations.
  **Reason:** the test class was testing infrastructure wiring on the aggregate, which is
  the design problem being fixed.
- Application-layer service test (existing class testing the service that calls
  `rule.evaluate(...)`) — update to verify that `DelayApplier`, `BehaviorEventLogger`, and
  exhaustion handling are invoked by the service, not by the rule.
  **Reason:** responsibility has moved from aggregate to application service.

**ArchUnit changes:**

- Add rule to `ArchitectureTest`: classes in `domain.behavior.model` must not have fields
  whose declared type resides in `domain.behavior.service`. **Reason:** prevents future
  aggregates from re-acquiring infrastructure-interface collaborators; makes the structural
  prohibition machine-checked.

Prerequisites: DDD-01

---

### DDD-03 — PersonId value object in navigation models

**Finding:** V-1 (High) + L-3 (Medium) — `Patient.personId`, `Message.personId`,
`Revocation.personId`, and `StatusUpdate.personId` are raw `String` fields. `PersonId` already
exists as a domain value object with normalization and matching logic. Using raw strings forces
callers to remember to normalize before comparison and scatters that knowledge across the codebase.
Repository interfaces reinforcing this by naming the parameter `normalizedPersonId` (a hint to
callers, not a type guarantee).

**New work:**

- Change the `personId` field in `Patient`, `Message`, `Revocation`, and `StatusUpdate` from
  `String` to `PersonId`.
- Update all `*NavigationRepository` interface methods that accept `String normalizedPersonId`
  to accept `PersonId personId` instead (`PatientNavigationRepository`,
  `MessageNavigationRepository`, `RevocationNavigationRepository`,
  `StatusUpdateNavigationRepository`).
- Update all repository implementations in `infrastructure/repository/navigation/` accordingly.
- Update the converters in `application/` that construct these domain objects — extract the
  raw string from the JAXB type and wrap it in `PersonId.of(raw)`.
- Update HATEOAS assemblers that read `personId` from domain objects to call
  `entity.getPersonId().value()` (or `normalized()`) when building URL path segments.

**Test changes:**

- `PatientNavigationRepositoryImplTest`, `MessageNavigationRepositoryImplTest`,
  `RevocationNavigationRepositoryImplTest`, `StatusUpdateNavigationRepositoryImplTest` —
  update `findByPersonId(...)` call sites to pass a `PersonId` instance instead of a raw
  `String`. **Reason:** method signature change — the parameter type is now `PersonId`.
- Converter unit tests — update assertions to unwrap `PersonId.value()` when comparing.
  **Reason:** field type change on the constructed domain object.
- Integration tests that assert on `personId` values in JSON responses are unaffected
  (the assembler serialises the string value, not the object).

**ArchUnit changes:**

- Add rule to `ArchitectureTest`: fields named `personId` in classes residing in
  `domain.navigation.model` must be of type `PersonId`. **Reason:** prevents future navigation
  model classes from introducing raw-string person IDs.

Prerequisites: none (independent of DDD-01 and DDD-02)

---

### DDD-04 — MatchCriteria all-null guard

**Finding:** V-3 (Low) — `MatchCriteria` with all fields null is semantically identical to a
`null` criteria reference on `BehaviorRule`, but is a distinct object. Callers can accidentally
construct an all-null `MatchCriteria` and get catch-all behaviour without intending to. The
canonical representation of "no criteria" is `null`; an all-null `MatchCriteria` object should
not exist.

**New work:**

- Add a compact constructor validation to `MatchCriteria` (or to the Lombok builder's `build()`):
  if all three fields are null, throw `IllegalArgumentException` with the message
  `"MatchCriteria requires at least one non-null field; use null criteria for catch-all"`.

**Test changes:**

- `MatchCriteriaTest` — remove the test that constructs an all-null `MatchCriteria` and asserts
  it matches any context. **Reason:** all-null construction is now illegal; the concept it
  tested (catch-all) belongs on a null criteria reference, not on an all-null object.
- `MatchCriteriaTest` — add test: constructing with all fields null throws
  `IllegalArgumentException`. **Reason:** new guard, assert the error path.
- `BehaviorRuleTest` — inspect whether any test constructs a `BehaviorRule` with an all-null
  `MatchCriteria`; if so, replace with `null` criteria. **Reason:** constructor of
  `MatchCriteria` now rejects the all-null case.

**ArchUnit changes:** none.

Prerequisites: none

---

### DDD-05 — BehaviorRule semantic method renames

**Finding:** L-4 (Low) — `BehaviorRule.hasErrorEffect()` and `BehaviorRule.hasDelay()` describe
internal state using predicate naming (`has*`) rather than expressing the rule's intended effect
from the domain's perspective.

**New work:**

- Rename `hasErrorEffect()` → `returnsError()`.
- Rename `hasDelay()` → `appliesDelay()`.
- Update all call sites in the domain, application layer, and tests.

**Test changes:**

- `BehaviorRuleTest` — rename test methods and call sites that reference `hasErrorEffect()` and
  `hasDelay()`. **Reason:** renamed production methods.
- Application-layer tests — update any call sites. **Reason:** same.

**ArchUnit changes:** none.

Prerequisites: DDD-02 recommended first (DDD-02 restructures `evaluate()` and may move or inline
these predicates; doing renames after the structure is stable avoids renaming twice).

---

### DDD-06 — Rename `EvaluationResult` to `MockResponse`

**Finding:** L-2 (Low) — `EvaluationResult` names a computation outcome rather than the domain
concept: what a mock service is configured to return to a SOAP caller. `MockResponse` expresses
that intent directly.

**New work:**

- Rename class `EvaluationResult` → `MockResponse` in `domain/behavior/model/`.
- Update all usages in the domain model, `RuleEvaluation` (introduced in DDD-02), application
  layer, and tests.

**Test changes:**

- `BehaviorRuleTest` and `BehaviorRuleEvaluationTest` — rename all references to
  `EvaluationResult`. **Reason:** class renamed.
- Application-layer tests — rename usages. **Reason:** same.

**ArchUnit changes:** none.

Prerequisites: DDD-02 (DDD-02 introduces `RuleEvaluation` which wraps `EvaluationResult`; rename
after that structure is in place to avoid two passes).

---

### DDD-07 — Rename `LogEntry` to `AuditLogEntry`

**Finding:** L-5 (Low) — `LogEntry` is ambiguous (could refer to application logging). The
domain concept is an audit log entry originating from the StoreLog SOAP service. `AuditLogEntry`
is unambiguous.

**New work:**

- Rename `LogEntry` → `AuditLogEntry` in `domain/navigation/model/`.
- Rename `LogEntryNavigationRepository` → `AuditLogEntryNavigationRepository`.
- Update all usages: infrastructure implementation, application service, assembler, controller,
  and all references in response records.

**Test changes:**

- All test classes that reference `LogEntry` or `LogEntryNavigationRepository` — rename
  references. **Reason:** class and interface renamed.
- Integration tests that post StoreLog SOAP and query `/api/navigate/log-entries` are unaffected
  at the HTTP level (URL path is unchanged); only Java type references change in the test helper
  code. **Reason:** same rename propagation.

**ArchUnit changes:** none.

Prerequisites: none (independent rename; can be done in any order relative to other DDD items).

---

## Dependency Graph

```
DDD-01 (BehaviorRule invariant)
  └── DDD-02 (Decouple BehaviorRule from infrastructure)
        ├── DDD-05 (Semantic method renames — do after DDD-02 stabilises evaluate())
        └── DDD-06 (EvaluationResult → MockResponse — do after DDD-02 wraps it in RuleEvaluation)

DDD-03 (PersonId in navigation models)   — independent
DDD-04 (MatchCriteria all-null guard)    — independent
DDD-07 (LogEntry → AuditLogEntry)        — independent
```

DDD-03, DDD-04, and DDD-07 can be implemented in any order, in parallel with the DDD-01/02
chain if desired.
