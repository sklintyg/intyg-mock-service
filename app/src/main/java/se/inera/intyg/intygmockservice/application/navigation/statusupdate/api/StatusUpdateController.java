package se.inera.intyg.intygmockservice.application.navigation.statusupdate.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygmockservice.application.navigation.statusupdate.StatusUpdateAssembler;
import se.inera.intyg.intygmockservice.application.navigation.statusupdate.StatusUpdateNavigationService;
import se.inera.intyg.intygmockservice.application.navigation.statusupdate.StatusUpdateResponse;

@RestController
@RequestMapping("/api/navigate")
@RequiredArgsConstructor
@Tag(name = "Navigate — Status Updates", description = "HATEOAS navigation API for status updates")
public class StatusUpdateController {

  private final StatusUpdateNavigationService service;
  private final StatusUpdateAssembler assembler;

  @Operation(summary = "List all status updates")
  @GetMapping("/status-updates")
  public CollectionModel<EntityModel<StatusUpdateResponse>> getAllStatusUpdates() {
    return assembler.toCollectionModel(service.findAll());
  }

  @Operation(summary = "List status updates for a certificate")
  @GetMapping("/certificates/{certificateId}/status-updates")
  public CollectionModel<EntityModel<StatusUpdateResponse>> getCertificateStatusUpdates(
      @PathVariable final String certificateId) {
    return assembler.toCollectionModel(service.findByCertificateId(certificateId));
  }
}
