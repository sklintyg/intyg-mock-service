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

@RestController
@RequestMapping("/api/navigate/certificates")
@RequiredArgsConstructor
@Tag(name = "Navigate — Certificates", description = "HATEOAS navigation API for certificates")
public class CertificateController {

  private final CertificateNavigationService service;
  private final CertificateAssembler assembler;

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

  @Operation(summary = "Get messages for a certificate (see /api/navigate/messages)")
  @GetMapping("/{certificateId}/messages")
  public ResponseEntity<CollectionModel<EntityModel<?>>> getCertificateMessages(
      @PathVariable final String certificateId) {
    return ResponseEntity.ok(CollectionModel.empty());
  }

  @Operation(summary = "Get status updates for a certificate (see /api/navigate/status-updates)")
  @GetMapping("/{certificateId}/status-updates")
  public ResponseEntity<CollectionModel<EntityModel<?>>> getCertificateStatusUpdates(
      @PathVariable final String certificateId) {
    return ResponseEntity.ok(CollectionModel.empty());
  }

  @Operation(summary = "Get log entries for a certificate (see /api/navigate/log-entries)")
  @GetMapping("/{certificateId}/log-entries")
  public ResponseEntity<CollectionModel<EntityModel<?>>> getCertificateLogEntries(
      @PathVariable final String certificateId) {
    return ResponseEntity.ok(CollectionModel.empty());
  }

  @Operation(
      summary = "Get revocation for a certificate (see /api/navigate/certificates/{id}/revocation)")
  @GetMapping("/{certificateId}/revocation")
  public ResponseEntity<EntityModel<?>> getCertificateRevocation(
      @PathVariable final String certificateId) {
    return ResponseEntity.notFound().build();
  }
}
