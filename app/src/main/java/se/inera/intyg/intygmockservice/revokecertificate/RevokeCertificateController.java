package se.inera.intyg.intygmockservice.revokecertificate;

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
import se.inera.intyg.intygmockservice.common.dto.CountResponse;
import se.inera.intyg.intygmockservice.revokecertificate.dto.RevokeCertificateDTO;

@RestController
@RequestMapping("/api/revoke-certificate")
@RequiredArgsConstructor
@Tag(name = "RevokeCertificate", description = "API for managing certificate revocations")
public class RevokeCertificateController {

  private final RevokeCertificateService service;

  @Operation(
      summary = "Get all revoke certificates",
      description = "Retrieve all revoke certificates")
  @GetMapping
  public List<RevokeCertificateDTO> getAllRevokeCertificates() {
    return service.getAll();
  }

  @Operation(summary = "Get count of stored revoke-certificate calls")
  @GetMapping("/count")
  public ResponseEntity<CountResponse> getCount() {
    return ResponseEntity.ok(new CountResponse(service.getCount()));
  }

  @Operation(
      summary = "Delete all revoke certificates",
      description = "Delete all revoke certificates")
  @DeleteMapping
  public void deleteAllRevokeCertificates() {
    service.deleteAll();
  }

  @Operation(
      summary = "Get revoke certificate by certificate ID",
      description = "Retrieve a single revoke certificate by its certificate ID")
  @GetMapping("/{certificateId}")
  public ResponseEntity<RevokeCertificateDTO> getRevokeCertificateById(
      @PathVariable final String certificateId) {
    return service
        .getById(certificateId)
        .map(ResponseEntity::ok)
        .orElseGet(ResponseEntity.notFound()::build);
  }

  @Operation(
      summary = "Delete revoke certificate by certificate ID",
      description = "Delete a single revoke certificate by its certificate ID")
  @DeleteMapping("/{certificateId}")
  public void deleteRevokeCertificateById(@PathVariable final String certificateId) {
    service.deleteById(certificateId);
  }

  @Operation(
      summary = "Get revoke certificates by logical address",
      description = "Retrieve all revoke certificates for a given logical address")
  @GetMapping("/logical-address/{logicalAddress}")
  public List<RevokeCertificateDTO> getRevokeCertificatesByLogicalAddress(
      @PathVariable final String logicalAddress) {
    return service.getByLogicalAddress(logicalAddress);
  }

  @Operation(
      summary = "Get revoke certificates by person ID",
      description = "Retrieve all revoke certificates for a given person ID (hyphens ignored)")
  @GetMapping("/person/{personId}")
  public List<RevokeCertificateDTO> getRevokeCertificatesByPersonId(
      @PathVariable final String personId) {
    return service.getByPersonId(personId);
  }
}
