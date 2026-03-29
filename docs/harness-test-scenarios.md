# Claude Code Harness — Test Scenarios

Seven scenarios to verify that hooks, commands, git hooks, and rules guidance all work as
intended. Work through them after any change to the harness.

---

## Scenario 1 — Post-edit hook: auto-format Java

**Goal:** Confirm the PostToolUse hook applies `spotlessApply` after Claude edits a `.java` file.

**Steps:**
- [ ] Ask Claude: _"Add a field `String foo;` to `RegisterCertificateDTO` with intentionally bad indentation (extra spaces, wrong column)"_
- [ ] Watch the status bar — you should see "Applying Google Java Format..."
- [ ] Run `./gradlew spotlessCheck` yourself

**Pass:** `spotlessCheck` exits 0 — the hook self-healed the formatting without you doing anything.
**Fail:** `spotlessCheck` reports violations — the hook did not fire or failed silently.

**Clean up:** Revert the change with `git checkout -- <file>`.

---

## Scenario 2 — Pre-commit gate: block non-compiling code

**Goal:** Confirm the PreToolUse hook intercepts `git commit` when the code does not compile.

**Steps:**
- [ ] Ask Claude: _"Add a method to `RegisterCertificateService` that references a type `NonExistentType` that doesn't exist anywhere, then commit the change"_
- [ ] Observe whether the commit is intercepted before it runs

**Pass:** Hook output shows a compile error; no commit is created (`git log` unchanged).
**Fail:** Commit proceeds despite broken code.

**Clean up:** `git checkout -- <file>`.

---

## Scenario 3 — Git pre-commit hook: enforced from the terminal

**Goal:** Confirm `.githooks/pre-commit` blocks an unformatted commit even outside Claude Code.

**Steps:**
- [ ] Run `./gradlew build` (installs the hook into `.git/hooks/`)
- [ ] Verify the hook exists: `ls -la .git/hooks/pre-commit`
- [ ] Open any `.java` file and remove indentation from one method body (break formatting manually)
- [ ] `git add <file>` and run `git commit -m "test"` in the terminal

**Pass:** Pre-commit prints "Pre-commit: checking formatting…" and exits non-zero; commit is blocked.
**Fail:** Commit goes through with bad formatting.

**Clean up:** `git checkout -- <file>`.

---

## Scenario 4 — /quality-check: full pipeline runs in sequence

**Goal:** Confirm the quality-check script runs all three CI stages and stops on the first failure.

**Steps:**
- [ ] Run `/quality-check`
- [ ] Observe the step labels: "Step 1/3: Checking formatting…", "Step 2/3: Building…", "Step 3/3: Running integration tests…"
- [ ] Verify the final line is "All quality checks passed."

**Pass:** All three steps complete in order; final line printed.
**Fail:** Step is skipped, output is missing, or script does not stop on failure.

**Bonus check:**
- [ ] Introduce bad formatting in a `.java` file (don't run `spotlessApply`)
- [ ] Run `/quality-check` again
- [ ] **Pass:** Script stops at step 1 with a format error and does not proceed to step 2

---

## Scenario 5 — /pre-commit-review: catches rule violations in staged code

**Goal:** Confirm the review command identifies violations before a commit.

**Setup:** Create (or edit) a temporary Java file that contains two deliberate violations:
```java
@Autowired  // violates code-style.md — must use @RequiredArgsConstructor
private SomeService service;

log.info("Stored certificate " + id);  // violates logging format — must use log.atInfo()
```

**Steps:**
- [ ] Stage the file: `git add <file>`
- [ ] Run `/pre-commit-review`

**Pass:** Review output names both violations (`@Autowired` and `log.info` with concatenation); does **not** say "everything looks clean".
**Fail:** Either violation is missed, or the review returns a false clean result.

**Clean up:** `git checkout -- <file>` or `git restore --staged <file>`.

---

## Scenario 6 — Rules guidance: Claude writes conformant code unprompted

**Goal:** Confirm rules files steer Claude toward correct code without explicit instructions.

**Steps:**
- [ ] Ask Claude: _"Add a method to `RegisterCertificateService` that returns the count of stored certificates. Add a unit test for it."_
- [ ] Review the generated production code:
  - [ ] Logging uses `log.atInfo().setMessage(...).addKeyValue(...).log()` — not `log.info(...)`
  - [ ] Local variables use `final var`
  - [ ] No `@Autowired` present
- [ ] Review the generated test:
  - [ ] Uses `@ExtendWith(MockitoExtension.class)` — no Spring context loaded
  - [ ] Follows Arrange / Act / Assert structure
  - [ ] No comments added unnecessarily

**Pass:** All checklist items satisfied without you needing to correct Claude.
**Fail:** Any item violated — note which rule file covers it and whether it needs strengthening.

**Clean up:** Revert the change unless you want to keep it.

---

## Scenario 7 — /add-soap-service: scaffolds all layers

**Goal:** Confirm the command produces a complete, compilable service skeleton that follows the architecture rules.

**Steps:**
- [ ] Run `/add-soap-service TestService`
- [ ] Check that these files were created:
  - [ ] `app/src/main/java/.../application/testservice/TestServiceResponderImpl.java`
  - [ ] `app/src/main/java/.../application/testservice/TestServiceService.java`
  - [ ] `app/src/main/java/.../application/testservice/TestServiceController.java`
  - [ ] `app/src/main/java/.../application/testservice/TestServiceConverter.java`
  - [ ] `app/src/main/java/.../application/testservice/TestServiceDTO.java`
  - [ ] `app/src/main/java/.../infrastructure/repository/TestServiceRepository.java`
  - [ ] `CxfConfig.java` updated with a new endpoint
  - [ ] Unit test classes in `app/src/test/` for each new class
- [ ] Run `./gradlew :app:test`

**Pass:** `./gradlew :app:test` exits 0; all files present with correct annotations.
**Fail:** Missing layer, compile error, or no tests generated.

**Clean up:** Delete the generated files and revert `CxfConfig.java`.

---

## After Running the Scenarios

For each **Fail** result, note:
- Which scenario failed
- What the actual output was
- Which harness file to update (`settings.json`, a rules file, a command file)

Feed findings back into the harness in the same session so they are covered by the next run.
