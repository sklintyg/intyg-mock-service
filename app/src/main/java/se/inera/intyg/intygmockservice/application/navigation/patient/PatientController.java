package se.inera.intyg.intygmockservice.application.navigation.patient;

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
import se.inera.intyg.intygmockservice.application.navigation.message.MessageAssembler;
import se.inera.intyg.intygmockservice.application.navigation.message.MessageNavigationService;
import se.inera.intyg.intygmockservice.application.navigation.message.MessageResponse;

@RestController
@RequestMapping("/api/navigate/patients")
@RequiredArgsConstructor
@Tag(name = "Navigate — Patients", description = "HATEOAS navigation API for patients")
public class PatientController {

  private final PatientNavigationService service;
  private final PatientAssembler assembler;
  private final CertificateAssembler certificateAssembler;
  private final MessageNavigationService messageNavigationService;
  private final MessageAssembler messageAssembler;

  @Operation(summary = "List all patients known to the service")
  @GetMapping
  public CollectionModel<EntityModel<PatientResponse>> getAllPatients() {
    return assembler.toCollectionModel(service.getAll());
  }

  @Operation(summary = "Get a patient by person ID (normalised, no dashes)")
  @GetMapping("/{personId}")
  public ResponseEntity<EntityModel<PatientResponse>> getPatientByPersonId(
      @PathVariable final String personId) {
    return service
        .findByPersonId(personId)
        .map(assembler::toModel)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(summary = "List certificates for a patient")
  @GetMapping("/{personId}/certificates")
  public CollectionModel<EntityModel<CertificateResponse>> getPatientCertificates(
      @PathVariable final String personId) {
    return certificateAssembler.toCollectionModel(service.findCertificatesByPersonId(personId));
  }

  @Operation(summary = "List messages for a patient")
  @GetMapping("/{personId}/messages")
  public CollectionModel<EntityModel<MessageResponse>> getPatientMessages(
      @PathVariable final String personId) {
    return messageAssembler.toCollectionModel(messageNavigationService.findByPersonId(personId));
  }
}
