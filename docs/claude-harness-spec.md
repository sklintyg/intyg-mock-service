# Claude Code Harness Specification — intyg-mock-service

## Context

The repository has a basic CLAUDE.md and a single unit-test skill, but no modular rules, slash
commands, hooks, or git hooks. The current CLAUDE.md mixes orientation info (build commands,
module layout) with detailed conventions (JAXB naming, logging format, layer patterns). Both
`CLAUDE.md` and `/.claude/` are fully gitignored, making the harness developer-local only.

This spec designs a harness that gives the agent strong project awareness, self-validation via
Gradle, and guardrails against common mistakes — while keeping the first iteration lean.

## Decisions

- **Git tracking:** Un-ignore `.claude/` and `CLAUDE.md` — make them team artifacts. Only
  `settings.local.json` and `memory/` stay gitignored.
- **Quality gates:** Reference future `quickCheck`/`qualityGate` tasks in the harness but don't
  create them. Design around current tasks (`build`, `spotlessCheck`, `integrationTest`).
- **Post-edit hook cost:** Accept ~2s per edit. Formatting correctness wins over speed.
- **Module layout:** Three Gradle modules: `app`, `domain`, `integration-test`. The `domain` module
  contains `behavior` and `navigation` packages (Spring-free domain logic).
- **Commands:** `quality-check` as an executable shell script; `add-soap-service` and
  `pre-commit-review` as prompt templates.
- **Flyway:** Not planned — no migrations rule needed.

---

## Proposed Directory Tree

```
intyg-mock-service/
├── CLAUDE.md                              # Slimmed: build commands, module layout, workflow rules
├── .claude/
│   ├── settings.json                      # Project-level: shared hook config + permissions (committed)
│   ├── settings.local.json                # Developer-local: personal overrides (gitignored, exists)
│   ├── rules/
│   │   ├── architecture.md                # Hexagonal layers, 3-module layout, per-service pattern
│   │   ├── code-style.md                  # Lombok, immutability, logging format, constructor injection
│   │   ├── testing-policy.md              # When to test, coverage mapping, TDD workflow
│   │   └── soap-jaxb.md                   # JAXB naming quirks, namespace handling, schema notes
│   ├── commands/
│   │   ├── add-soap-service.md            # Scaffold all layers for a new SOAP service (prompt)
│   │   ├── quality-check.sh               # Run CI-equivalent pipeline locally (script)
│   │   └── pre-commit-review.md           # Review staged changes before committing (prompt)
│   ├── skills/
│   │   └── unit-test/
│   │       └── SKILL.md                   # Existing — no changes
│   └── hooks/                             # (empty dir — hooks configured in settings.json)
└── .githooks/
    └── pre-commit                         # spotlessCheck + compileJava for all developers
```

**New files: 10** | **Modified files: 3** (CLAUDE.md, .gitignore, build.gradle)

---

## Content Outline per File

### CLAUDE.md (refactored)

**Stays:**
- Build & Run commands (unchanged)
- Java version note
- Module Layout: three Gradle modules — `app` (Spring Boot), `domain` (pure logic, no Spring),
  `integration-test`
- Architecture summary (3 sentences: hexagonal, 5 SOAP services, behavior engine)
- Workflow section (commit conventions, backlog handling)
- Pointer: "Detailed conventions in `.claude/rules/`"

**Moves out:**

| Content | Destination |
|---|---|
| Per-service layer pattern details | `rules/architecture.md` |
| Common infrastructure (AbstractInMemoryRepository) | `rules/architecture.md` |
| Code style (Lombok, final, logging) | `rules/code-style.md` |
| Key Schema Notes (JAXB/RIV-TA) | `rules/soap-jaxb.md` |
| Testing strategy details | `rules/testing-policy.md` |

**Rationale:** Build commands and module layout are orientation — needed every session. Conventions
are contextual — needed only when writing specific types of code. Rules files load on demand.

---

### .claude/rules/architecture.md

- **Three-module layout:** `domain` (pure logic, no Spring deps), `app` (Spring Boot, depends on
  domain), `integration-test` (E2E tests, depends on app JAR)
- Hexagonal layers within `app`: `application/` (use cases + SOAP endpoints), `infrastructure/`
  (adapters, config, persistence)
- Per-service pattern table: ResponderImpl → Service → Controller → Converter → Repository → DTO
  with one-line responsibility per role
- `CxfConfig` wiring: new services must be manually registered, all SOAP paths prefixed `/services/`
- Package isolation: feature packages under `application/` must not depend on each other; `common/`
  is shared
- Navigation layer: HATEOAS controllers assemble links from per-service repositories, do not own data
- Passthrough pattern: optional per-service mTLS passthrough via `infrastructure/passthrough/`
- Reference to `ArchitectureTest.java` — the rules above are mechanically enforced

---

### .claude/rules/code-style.md

- DTOs: `@Value @Builder` (Lombok), no setters, no mutable fields
- `final` on local variables (`final var`), fields, and parameters wherever possible
- Constructor injection via `@RequiredArgsConstructor`, no `@Autowired`
- Logging: `log.atInfo().setMessage("...").addKeyValue("key", value).log()` — no string concat,
  no `log.info()` style
- Google Java Format via Spotless — run `spotlessApply` before committing; never fight the formatter
- No wildcard imports, no comments unless logic is non-obvious
- Access modifiers: package-private by default for test classes and methods

---

### .claude/rules/testing-policy.md

- Every production class gets a mirror test class (except DTOs, `@Configuration`, main class)
- New classes: write tests. Behavior changes: TDD red-green. Bug fixes: regression test first.
- Unit tests: Mockito only, no Spring context — see `skills/unit-test/SKILL.md` for how
- Integration tests: one IT per SOAP service, `@SpringBootTest(RANDOM_PORT)` + `TestRestTemplate`
- Test data: SOAP request XML in `integration-test/src/test/resources/soap/`, expected responses
  in `expected/`
- Run unit tests: `./gradlew :app:test`
- Run integration tests: `./gradlew :integration-test:integrationTest`

---

### .claude/rules/soap-jaxb.md

- JAXB naming: hyphenated XML elements become camelCase — `intygs-id` → `getIntygsId()`,
  `personal-id` → `getPersonalId()`
- Inner `ns2:` (certificate:3) elements use hyphens in XML: `personal-id`, `enhets-id`,
  `vardgivare-id`, `arbetsplatskod`
- StoreLog quirk: certificate ID comes from `log.activity.activityLevel`, not the outer envelope
- RIV-TA response pattern: build `ResultType` with `ResultCodeType.OK`; errors use
  `ResultCodeType.ERROR` with `ErrorIdType`
- Schema dependencies: JAXB types come from `clinicalprocess-healthcond-certificate-schemas` and
  `informationsecurity-auditing-log-schemas` JARs — never create custom JAXB classes
- CXF endpoint path convention: `/services/<domain>/<service>/<version>/rivtabp21`

---

### .claude/commands/add-soap-service.md (prompt template)

Slash command `/add-soap-service <ServiceName>`. Scaffolds end-to-end using `RegisterCertificate`
as the reference implementation:

1. `app/src/main/java/.../application/<servicename>/` — ResponderImpl, Service, Controller,
   Converter, DTO
2. `app/src/main/java/.../infrastructure/repository/<ServiceName>Repository.java` extending
   `AbstractInMemoryRepository`
3. Register endpoint in `CxfConfig.java`
4. Add config in `application.yml` (passthrough toggle + repository max-size)
5. Unit tests for each new class
6. Integration test in `integration-test/` with SOAP request XML
7. Run `./gradlew spotlessApply` and `./gradlew :app:test`

---

### .claude/commands/quality-check.sh (shell script)

Executable script mirroring the CI pipeline from `Jenkins.properties`. Runs in sequence, stops on
first failure:

```bash
#!/usr/bin/env bash
set -euo pipefail
echo "Step 1/3: Checking formatting..."
./gradlew spotlessCheck --quiet
echo "Step 2/3: Building (compile + unit tests)..."
./gradlew build --quiet
echo "Step 3/3: Running integration tests..."
./gradlew :integration-test:integrationTest --quiet
echo "All quality checks passed."
```

When `qualityGate` task is added to the build, replace the three steps with
`./gradlew qualityGate`.

---

### .claude/commands/pre-commit-review.md (prompt template)

Slash command `/pre-commit-review`. Reviews staged changes before committing:

1. Run `git diff --cached --stat` and `git diff --cached` to inspect what is staged
2. Check for: missing unit tests for new production classes, wrong annotations, mutable fields,
   wrong logging pattern, JAXB getter mistakes
3. Run `./gradlew spotlessCheck` to verify formatting
4. Summarize findings; if clean, say so explicitly

---

## Hook Strategy

### Claude Code Hooks (configured in .claude/settings.json)

| Hook | Type | Trigger | Action | Failure Prevented |
|---|---|---|---|---|
| Post-edit format | PostToolUse | `Write`/`Edit` on `**/*.java` | `./gradlew spotlessApply --quiet` | Unformatted code entering working tree |
| Pre-commit gate | PreToolUse | `Bash` matching `git commit*` | `./gradlew compileJava spotlessCheck --quiet` | Committing non-compiling or unformatted code |
| Build config guard | PreToolUse | `Write`/`Edit` on build/CI files | Warning message printed | Accidental modification of shared CI config |

**Design decisions:**
- Post-edit uses `spotlessApply` (not `spotlessCheck`) — self-healing, avoids retry loops
- Pre-commit runs both compile and format check — last gate before a commit lands
- Build config guard is a warning only, not a block — the agent may legitimately need to edit these

### Git Hooks (in .githooks/, Gradle-managed)

**.githooks/pre-commit** guards all developers, not just Claude Code users:
- Skips early if no `.java` files are staged
- Runs `./gradlew spotlessCheck` then `./gradlew :app:compileJava`
- Exits non-zero on failure, blocking the commit

**Gradle wiring** in root `build.gradle`:

```groovy
tasks.register('installGitHooks', Copy) {
    from '.githooks'
    into '.git/hooks'
    fileMode 0755
}

tasks.named('build') {
    dependsOn 'installGitHooks'
}
```

Hooks are installed automatically on the first `./gradlew build`. No `core.hooksPath` needed —
the Copy approach is repo-scoped and avoids global git config side-effects.

---

## Files Outside .claude/ and .githooks/

These files require explicit approval before modification:

| File | Change | Reason |
|---|---|---|
| `.gitignore` | Replace `/.claude/` entry with `/.claude/settings.local.json` and `/.claude/memory/` | Allow rules, commands, skills, and settings.json to be committed |
| `CLAUDE.md` | Slim down; move convention details to rules | Keep as fast-scan orientation doc |
| `build.gradle` | Add `installGitHooks` task | Wire git hooks reproducibly via Gradle |

---

## .claude/settings.json (sketch)

```json
{
  "permissions": {
    "allow": [
      "Bash(./gradlew build*)",
      "Bash(./gradlew spotlessApply*)",
      "Bash(./gradlew spotlessCheck*)",
      "Bash(./gradlew :app:test*)",
      "Bash(./gradlew :integration-test:integrationTest*)",
      "Bash(./gradlew compileJava*)",
      "Bash(git*)"
    ]
  },
  "hooks": {
    "PostToolUse": [
      {
        "matcher": "Write|Edit",
        "filePattern": "**/*.java",
        "command": "./gradlew spotlessApply --quiet 2>&1 | tail -5"
      }
    ],
    "PreToolUse": [
      {
        "matcher": "Bash",
        "commandPattern": "git commit.*",
        "command": "./gradlew compileJava spotlessCheck --quiet 2>&1 | tail -10"
      }
    ]
  }
}
```

The exact hook schema will be validated against the Claude Code settings format during
implementation.

---

## Verification Plan

1. Run `/quality-check` — should execute spotlessCheck → build → integrationTest in sequence
2. Edit a Java file with bad formatting → verify post-edit hook auto-applies formatting
3. Stage a non-compiling change and run `git commit` → verify pre-commit gate blocks
4. Run `./gradlew build` on a fresh clone → verify `.git/hooks/pre-commit` is installed
5. Run `/pre-commit-review` with staged changes → verify it catches issues
6. Verify all existing tests still pass: `./gradlew build :integration-test:integrationTest`
