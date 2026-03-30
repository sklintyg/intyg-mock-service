package se.inera.intyg.intygmockservice.application.navigation.staff.api;

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
import se.inera.intyg.intygmockservice.application.navigation.staff.StaffAssembler;
import se.inera.intyg.intygmockservice.application.navigation.staff.StaffNavigationService;
import se.inera.intyg.intygmockservice.application.navigation.staff.StaffResponse;

@RestController
@RequestMapping("/api/navigate/staff")
@RequiredArgsConstructor
@Tag(name = "Navigate — Staff", description = "HATEOAS navigation API for health care staff")
public class StaffController {

  private final StaffNavigationService service;
  private final StaffAssembler assembler;
  private final CertificateAssembler certificateAssembler;

  @Operation(summary = "List all staff")
  @GetMapping
  public CollectionModel<EntityModel<StaffResponse>> getAllStaff() {
    return assembler.toCollectionModel(service.findAll());
  }

  @Operation(summary = "Get a staff member by staff ID")
  @GetMapping("/{staffId}")
  public ResponseEntity<EntityModel<StaffResponse>> getStaffById(
      @PathVariable final String staffId) {
    return service
        .findById(staffId)
        .map(assembler::toModel)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(summary = "List certificates issued by a staff member")
  @GetMapping("/{staffId}/certificates")
  public CollectionModel<EntityModel<CertificateResponse>> getStaffCertificates(
      @PathVariable final String staffId) {
    return certificateAssembler.toCollectionModel(service.findCertificatesByStaffId(staffId));
  }
}
