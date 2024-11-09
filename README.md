# Intyg Mock Service

## Overview

The Intyg Mock Service is a Spring Boot application designed to simulate the behavior of services
that Intygstjänster is dependent upon. This in order to make it testable.

## Prerequisites

- Java 21

## Building the Project

To build the project, run the following command:

```sh
./gradlew build
```

## Running the Project

To run the project, run the following command:

```sh
./gradlew appRun
```

## Mocked SOAP endpoints

See available cxfservices at <http://localhost:18888/services>.

## API

See swagger documentation at <http://localhost:18888/swagger-ui/index.html>.

## How to configure Intygstjänster to use the mock service

| Application  | SOAP Webservice                | Local environment (application-dev.properties)    | Test environment (configmap.yaml)                 | 
|--------------|--------------------------------|---------------------------------------------------|---------------------------------------------------|
| Webcert      | CertificateStatusUpdateForCare | certificatestatusupdateforcare.ws.endpoint.v3.url | CERTIFICATESTATUSUPDATEFORCARE_WS_ENDPOINT_V3_URL |
| Intygstjanst | RegisterCertificate            | registercertificatev3.endpoint.url                | REGISTERCERTIFICATEV3_ENDPOINT_URL                |
| Intygstjanst | RevokeCertificate              | revokecertificatev2.endpoint.url                  | REVOKECERTIFICATEV2_ENDPOINT_URL                  |
| Intygstjanst | SendMessageToRecipient         | sendmessagetocarev2.endpoint.url                  | SENDMESSAGETOCAREV2_ENDPOINT_URL                  |
