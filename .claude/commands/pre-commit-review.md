# Pre-Commit Review

Review staged changes before committing to catch common issues.

## Usage

```
/pre-commit-review
```

## Steps

1. Run `git diff --cached --stat` to see what is staged
2. Run `git diff --cached` to inspect the full diff
3. Check for the following issues:
   - New production classes without corresponding test classes
   - Missing `@Value @Builder` on DTOs; mutable fields or setters present
   - Wrong logging pattern — must use `log.atInfo().setMessage(...).addKeyValue(...).log()`, not `log.info(...)`
   - Wrong injection style — must use `@RequiredArgsConstructor`, not `@Autowired`
   - JAXB getter mistakes — hyphenated XML elements must use camelCase getters
   - `*ResponderImpl` doing business logic instead of delegating to `*Service`
   - `*Controller` doing business logic instead of delegating to `*Service`
   - Cross-package dependencies between `application/<servicename>/` packages
4. Run `./gradlew spotlessCheck` to verify formatting is clean
5. Summarize findings — if everything is clean, say so explicitly so the commit can proceed
