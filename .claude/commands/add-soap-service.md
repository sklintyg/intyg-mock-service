# Add SOAP Service

Scaffold all layers for a new SOAP service end-to-end. Use `RegisterCertificate` as the reference implementation.

## Usage

```
/add-soap-service <ServiceName>
```

Example: `/add-soap-service RevokeCertificate`

## Steps

1. Create `app/src/main/java/.../application/<servicename>/` with:
   - `<ServiceName>ResponderImpl.java` — implements the RIV-TA SOAP interface, delegates to service
   - `<ServiceName>Service.java` — business logic (store, convert, log)
   - `<ServiceName>Controller.java` — REST GET/DELETE inspection endpoints
   - `<ServiceName>Converter.java` — converts JAXB types to DTO
   - `<ServiceName>DTO.java` — `@Value @Builder` Lombok object

2. Create `app/src/main/java/.../infrastructure/repository/<ServiceName>Repository.java` extending `AbstractInMemoryRepository<T>`

3. Register the endpoint in `CxfConfig.java` — manually instantiate the bean and publish it as a CXF endpoint under `/services/<path>/rivtabp21`

4. Add configuration to `application.yml`:
   - Passthrough toggle: `app.passthrough.<servicename>.enabled: false`
   - Repository max-size: `app.repository.<servicename>.max-size: 1000`

5. Write unit tests for each new class in `app/src/test/` — see `.claude/rules/testing-policy.md` and `skills/unit-test/SKILL.md`

6. Write an integration test in `integration-test/src/test/java/` with SOAP request XML in `integration-test/src/test/resources/soap/`

7. Run `./gradlew spotlessApply` then `./gradlew :app:test` to verify everything compiles and tests pass

## Reference

See `.claude/rules/architecture.md` for the full per-service layer pattern and responsibility rules.
See `.claude/rules/soap-jaxb.md` for JAXB naming quirks and RIV-TA response patterns.
