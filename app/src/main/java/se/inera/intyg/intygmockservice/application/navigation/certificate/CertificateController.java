package se.inera.intyg.intygmockservice.application.navigation.certificate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygmockservice.application.navigation.logentry.LogEntryAssembler;
import se.inera.intyg.intygmockservice.application.navigation.logentry.LogEntryNavigationService;
import se.inera.intyg.intygmockservice.application.navigation.logentry.LogEntryResponse;
import se.inera.intyg.intygmockservice.application.navigation.message.MessageAssembler;
import se.inera.intyg.intygmockservice.application.navigation.message.MessageNavigationService;
import se.inera.intyg.intygmockservice.application.navigation.message.MessageResponse;

@RestController
@RequestMapping("/api/navigate/certificates")
@RequiredArgsConstructor
@Tag(name = "Navigate — Certificates", description = "HATEOAS navigation API for certificates")
public class CertificateController {

  private final CertificateNavigationService service;
  private final CertificateAssembler assembler;
  private final MessageNavigationService messageNavigationService;
  private final MessageAssembler messageAssembler;
  private final LogEntryNavigationService logEntryNavigationService;
  private final LogEntryAssembler logEntryAssembler;

  @Operation(summary = "List all certificates (merged view across all services)")
  @GetMapping
  public CollectionModel<EntityModel<CertificateResponse>> getAllCertificates() {
    return assembler.toCollectionModel(service.findAll());
  }

  @Operation(summary = "Get a certificate by ID")
  @GetMapping("/{certificateId}")
  public ResponseEntity<EntityModel<CertificateResponse>> getCertificateById(
      @PathVariable final String certificateId) {
    return service
        .findById(certificateId)
        .map(assembler::toModel)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(summary = "List messages for a certificate")
  @GetMapping("/{certificateId}/messages")
  public CollectionModel<EntityModel<MessageResponse>> getCertificateMessages(
      @PathVariable final String certificateId) {
    return messageAssembler.toCollectionModel(
        messageNavigationService.findByCertificateId(certificateId));
  }

  @Operation(summary = "List log entries for a certificate")
  @GetMapping("/{certificateId}/log-entries")
  public CollectionModel<EntityModel<LogEntryResponse>> getCertificateLogEntries(
      @PathVariable final String certificateId) {
    return logEntryAssembler.toCollectionModel(
        logEntryNavigationService.findByCertificateId(certificateId));
  }
}
