package se.inera.intyg.intygmockservice.application.registercertificate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygmockservice.application.common.dto.CountResponse;
import se.inera.intyg.intygmockservice.application.registercertificate.dto.RegisterCertificateDTO;
import se.inera.intyg.intygmockservice.application.registercertificate.service.RegisterCertificateService;

@RestController
@RequestMapping("/api/register-certificate")
@RequiredArgsConstructor
@Tag(
    name = "Mock — RegisterCertificate",
    description = "API for managing certificate registrations")
public class RegisterCertificateController {

  private final RegisterCertificateService registerCertificateService;

  @Operation(
      summary = "Get all registered certificates",
      description = "Retrieve all registered certificates")
  @GetMapping
  public List<RegisterCertificateDTO> getAllRegisteredCertificates() {
    return registerCertificateService.getAll();
  }

  @Operation(
      summary = "Get registered certificate as XML",
      description = "Retrieve a registered certificate as raw XML by certificate ID")
  @GetMapping(value = "/{certificateId}/xml", produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<String> getCertificateAsXml(@PathVariable final String certificateId) {
    return registerCertificateService
        .getAsXml(certificateId)
        .map(xml -> ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(xml))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(
      summary = "Get registered certificate by ID",
      description = "Retrieve a registered certificate as DTO by certificate ID")
  @GetMapping("/{certificateId}")
  public ResponseEntity<RegisterCertificateDTO> getCertificateById(
      @PathVariable final String certificateId) {
    return registerCertificateService
        .getById(certificateId)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(
      summary = "Get registered certificates by logical address",
      description = "Retrieve all registered certificates stored under a given logical address")
  @GetMapping("/logical-address/{logicalAddress}")
  public List<RegisterCertificateDTO> getCertificatesByLogicalAddress(
      @PathVariable final String logicalAddress) {
    return registerCertificateService.getByLogicalAddress(logicalAddress);
  }

  @Operation(
      summary = "Get registered certificates by person ID",
      description =
          "Retrieve all registered certificates for a patient person ID. Hyphens are normalised (191212121212 == 19121212-1212)")
  @GetMapping("/person/{personId}")
  public List<RegisterCertificateDTO> getCertificatesByPersonId(
      @PathVariable final String personId) {
    return registerCertificateService.getByPersonId(personId);
  }

  @Operation(summary = "Get count of stored register-certificate calls")
  @GetMapping("/count")
  public ResponseEntity<CountResponse> getCount() {
    return ResponseEntity.ok(new CountResponse(registerCertificateService.getCount()));
  }

  @Operation(
      summary = "Delete all registered certificates",
      description = "Delete all registered certificates")
  @DeleteMapping
  public void deleteAllRegisteredCertificates() {
    registerCertificateService.deleteAll();
  }

  @Operation(
      summary = "Delete registered certificate by ID",
      description = "Delete a specific registered certificate by certificate ID")
  @DeleteMapping("/{certificateId}")
  public void deleteCertificateById(@PathVariable final String certificateId) {
    registerCertificateService.deleteById(certificateId);
  }
}
