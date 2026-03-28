# SOAP / JAXB / RIV-TA

## JAXB Naming

JAXB maps hyphenated XML elements to camelCase Java getters:
- `intygs-id` → `getIntygsId()`
- `personal-id` → `getPersonalId()`
- `enhets-id` → `getEnhetsId()`
- `vardgivare-id` → `getVardgivareId()`

Inner `ns2:` (certificate:3) elements use hyphens in XML: `personal-id`, `enhets-id`, `vardgivare-id`, `arbetsplatskod`.

## Schema Dependencies

JAXB types come from these JARs — never create custom JAXB classes:
- `clinicalprocess-healthcond-certificate-schemas`
- `informationsecurity-auditing-log-schemas`

## StoreLog Quirk

The certificate ID in StoreLog comes from `log.activity.activityLevel`, **not** from the outer envelope. Using any other field is a bug.

## RIV-TA Response Pattern

Success response:
```java
ResultType result = new ResultType();
result.setResultCode(ResultCodeType.OK);
```

Error response:
```java
ResultType result = new ResultType();
result.setResultCode(ResultCodeType.ERROR);
result.setErrorId(ErrorIdType.APPLICATION_ERROR);
result.setResultText("Description of error");
```

## SOAP Request XML Element Order

Some types require a specific element order in XML (enforced by the schema):

- `RevokeCertificateType`: `meddelande`, `skickatTidpunkt`, `intygs-id`, `patientPerson-id`, `skickatAv`
- `SendMessageToRecipientType`: `meddelande-id`, `skickatTidpunkt`, `intygs-id`, `patientPerson-id`, `logiskAdressMottagare`, `amne`, `rubrik`, `meddelande`, `skickatAv`
