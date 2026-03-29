# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

Detailed conventions are in `.claude/rules/` — load the relevant file when writing specific types of code.

## Build & Run

```bash
./gradlew build                                         # Build all modules
./gradlew :app:test                                     # Run unit tests
./gradlew :integration-test:integrationTest             # Run integration tests
./gradlew build :integration-test:integrationTest       # Build + integration tests
./gradlew appRun                                        # Start app on port 18888
./gradlew spotlessApply                                 # Auto-format code (Google Java Format)
./gradlew spotlessCheck                                 # Check formatting without applying
```

To run a single integration test class:

```bash
./gradlew :integration-test:integrationTest --tests "se.inera.intyg.intygmockservice.storelog.StoreLogIT"
```

**Java:** Must use Java 21 (jenv: `zulu64-21.0.8`). The `.java-version` file may reference an unavailable version — run `./gradlew` directly.

## Module Layout

```
app/                         # Spring Boot application (CXF SOAP, REST, in-memory repos)
domain/                      # Pure domain logic — no Spring dependencies
integration-test/            # Integration tests (separate Gradle module)
```

`app/build.gradle` has `jar { enabled = true }` so the plain JAR is available to the `integration-test` module.

## Architecture

Spring Boot app that mocks 5 RIV-TA SOAP services. Each received SOAP message is stored in memory and exposed via REST inspection endpoints. Every SOAP service follows the same layer pattern: `ResponderImpl → Service → Controller → Converter → Repository → DTO`. See `.claude/rules/architecture.md` for details.

## Workflow

- **Commit messages:** Must follow the format `K1J-NNNN: Description` where the description starts with a capital letter. Example: `K1J-1927: Added a git hook that verifies commit messages`. Never include `Co-Authored-By` lines.
- **Product backlog:** When implementing items from `ProductBacklogSpecification.md`, mark them as done (strikethrough + ✓ Done) in the same commit.
