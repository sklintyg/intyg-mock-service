# Testing Policy

## Coverage Mapping

Every production class gets a mirror test class, **except:**
- DTOs (`@Value @Builder`) — no logic to test
- `@Configuration` classes
- The main application class

## When to Write Tests

| Situation | Action |
|---|---|
| New class | Write tests for it |
| Behaviour change | TDD: write failing test first, then fix |
| Bug fix | Write a regression test that fails before the fix |

## Unit Tests (`app/src/test/`)

- One test class per production class
- Mock all dependencies with Mockito (`@ExtendWith(MockitoExtension.class)`)
- No Spring context — pure unit tests
- See `skills/unit-test/SKILL.md` for the canonical how-to

```bash
./gradlew :app:test
```

To run a single class:
```bash
./gradlew :app:test --tests "se.inera.intyg.intygmockservice.registercertificate.RegisterCertificateServiceTest"
```

## Integration Tests (`integration-test/`)

- One IT class per SOAP service
- `@SpringBootTest(webEnvironment = RANDOM_PORT)` + `TestRestTemplate`
- SOAP request XML in `integration-test/src/test/resources/soap/`
- Expected response XML in `integration-test/src/test/resources/expected/`

```bash
./gradlew :integration-test:integrationTest
```

To run a single class:
```bash
./gradlew :integration-test:integrationTest --tests "se.inera.intyg.intygmockservice.storelog.StoreLogIT"
```
