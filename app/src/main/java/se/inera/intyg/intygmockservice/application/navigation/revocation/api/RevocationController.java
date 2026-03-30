package se.inera.intyg.intygmockservice.application.navigation.revocation.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygmockservice.application.navigation.revocation.RevocationAssembler;
import se.inera.intyg.intygmockservice.application.navigation.revocation.RevocationNavigationService;
import se.inera.intyg.intygmockservice.application.navigation.revocation.RevocationResponse;

@RestController
@RequestMapping("/api/navigate/certificates")
@RequiredArgsConstructor
@Tag(name = "Navigate — Revocations", description = "HATEOAS navigation API for revocations")
public class RevocationController {

  private final RevocationNavigationService service;
  private final RevocationAssembler assembler;

  @Operation(summary = "Get revocation for a certificate")
  @GetMapping("/{certificateId}/revocation")
  public ResponseEntity<EntityModel<RevocationResponse>> getCertificateRevocation(
      @PathVariable final String certificateId) {
    return service
        .findByCertificateId(certificateId)
        .map(assembler::toModel)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
