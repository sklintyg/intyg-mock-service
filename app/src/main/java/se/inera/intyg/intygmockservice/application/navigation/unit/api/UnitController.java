package se.inera.intyg.intygmockservice.application.navigation.unit.api;

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
import se.inera.intyg.intygmockservice.application.navigation.certificate.CertificateAssembler;
import se.inera.intyg.intygmockservice.application.navigation.certificate.CertificateResponse;
import se.inera.intyg.intygmockservice.application.navigation.unit.UnitAssembler;
import se.inera.intyg.intygmockservice.application.navigation.unit.UnitNavigationService;
import se.inera.intyg.intygmockservice.application.navigation.unit.UnitResponse;

@RestController
@RequestMapping("/api/navigate/units")
@RequiredArgsConstructor
@Tag(name = "Navigate — Units", description = "HATEOAS navigation API for care units")
public class UnitController {

  private final UnitNavigationService service;
  private final UnitAssembler assembler;
  private final CertificateAssembler certificateAssembler;

  @Operation(summary = "List all units")
  @GetMapping
  public CollectionModel<EntityModel<UnitResponse>> getAllUnits() {
    return assembler.toCollectionModel(service.findAll());
  }

  @Operation(summary = "Get a unit by unit ID")
  @GetMapping("/{unitId}")
  public ResponseEntity<EntityModel<UnitResponse>> getUnitById(@PathVariable final String unitId) {
    return service
        .findById(unitId)
        .map(assembler::toModel)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(summary = "List certificates for a unit")
  @GetMapping("/{unitId}/certificates")
  public CollectionModel<EntityModel<CertificateResponse>> getUnitCertificates(
      @PathVariable final String unitId) {
    return certificateAssembler.toCollectionModel(service.findCertificatesByUnitId(unitId));
  }
}
