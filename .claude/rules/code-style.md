# Code Style

## DTOs

Use `@Value @Builder` (Lombok). No setters, no mutable fields.

```java
@Value
@Builder
public class RegisterCertificateDTO {
    String certificateId;
    String careUnitId;
}
```

## Immutability

Use `final` on local variables (`final var`), fields, and parameters wherever possible. Avoid unnecessary mutability.

## Dependency Injection

Constructor injection via `@RequiredArgsConstructor`. Never use `@Autowired`.

```java
@Service
@RequiredArgsConstructor
public class RegisterCertificateService {
    private final RegisterCertificateRepository repository;
    private final RegisterCertificateConverter converter;
}
```

## Logging

Use structured ECS-format logging. Never use string concatenation or the `log.info()` shorthand.

```java
log.atInfo()
    .setMessage("Stored certificate")
    .addKeyValue("certificateId", dto.getCertificateId())
    .log();
```

## Formatting

Google Java Format is enforced via Spotless. Run `./gradlew spotlessApply` before committing. Never manually fight the formatter — let it win.

## Other Rules

- No wildcard imports
- No comments unless the logic is genuinely non-obvious
- Package-private access by default for test classes and test methods
- Access modifiers: prefer the narrowest scope that works
