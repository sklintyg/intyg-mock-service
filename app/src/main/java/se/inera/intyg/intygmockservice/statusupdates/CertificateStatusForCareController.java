package se.inera.intyg.intygmockservice.statusupdates;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygmockservice.statusupdates.dto.CertificateStatusUpdateForCareDTO;

@RestController()
@RequestMapping("/api/certificate-status-for-care")
@RequiredArgsConstructor
@Tag(
    name = "CertificateStatusForCare",
    description = "API for managing certificate status updates for care")
public class CertificateStatusForCareController {

  private final CertificateStatusUpdateForCareService service;

  @Operation(
      summary = "Get all certificate status updates",
      description = "Retrieve all certificate status updates for care")
  @GetMapping
  public List<CertificateStatusUpdateForCareDTO> getAllCertificateStatusUpdates() {
    return service.getAll();
  }

  @Operation(
      summary = "Delete all certificate status updates",
      description = "Delete all certificate status updates for care")
  @DeleteMapping
  public void deleteAllCertificateStatusUpdates() {
    service.deleteAll();
  }

  @Operation(
      summary = "Get certificate status updates by certificate ID",
      description = "Retrieve all certificate status updates for a given certificate ID")
  @GetMapping("/{certificateId}")
  public List<CertificateStatusUpdateForCareDTO> getByCertificateId(
      @PathVariable final String certificateId) {
    return service.getByCertificateId(certificateId);
  }

  @Operation(
      summary = "Delete certificate status updates by certificate ID",
      description = "Delete all certificate status updates for a given certificate ID")
  @DeleteMapping("/{certificateId}")
  public ResponseEntity<Void> deleteByCertificateId(@PathVariable final String certificateId) {
    service.deleteByCertificateId(certificateId);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Get certificate status updates by logical address",
      description = "Retrieve all certificate status updates for a given logical address")
  @GetMapping("/logical-address/{logicalAddress}")
  public List<CertificateStatusUpdateForCareDTO> getByLogicalAddress(
      @PathVariable final String logicalAddress) {
    return service.getByLogicalAddress(logicalAddress);
  }

  @Operation(
      summary = "Get certificate status updates by person ID",
      description =
          "Retrieve all certificate status updates for a given person ID (hyphens ignored)")
  @GetMapping("/person/{personId}")
  public List<CertificateStatusUpdateForCareDTO> getByPersonId(
      @PathVariable final String personId) {
    return service.getByPersonId(personId);
  }

  @Operation(
      summary = "Get certificate status updates by event type code",
      description = "Retrieve all certificate status updates for a given event type code")
  @GetMapping("/event-type/{eventCode}")
  public List<CertificateStatusUpdateForCareDTO> getByEventCode(
      @PathVariable final String eventCode) {
    return service.getByEventCode(eventCode);
  }
}
