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
import se.inera.intyg.intygmockservice.revokecertificate.converter.RevokeCertificateConverter;
import se.inera.intyg.intygmockservice.revokecertificate.dto.RevokeCertificateDTO;
import se.inera.intyg.intygmockservice.revokecertificate.repository.RevokeCertificateRepository;

@RestController
@RequestMapping("/api/revoke-certificate")
@RequiredArgsConstructor
@Tag(name = "RevokeCertificate", description = "API for managing certificate revocations")
public class RevokeCertificateController {

  private final RevokeCertificateRepository repository;
  private final RevokeCertificateConverter revokeCertificateConverter;

  @Operation(
      summary = "Get all revoke certificates",
      description = "Retrieve all revoke certificates")
  @GetMapping
  public List<RevokeCertificateDTO> getAllRevokeCertificates() {
    return repository.findAll().stream().map(revokeCertificateConverter::convert).toList();
  }

  @Operation(
      summary = "Delete all revoke certificates",
      description = "Delete all revoke certificates")
  @DeleteMapping
  public void deleteAllRevokeCertificates() {
    repository.deleteAll();
  }

  @Operation(
      summary = "Get revoke certificate by certificate ID",
      description = "Retrieve a single revoke certificate by its certificate ID")
  @GetMapping("/{certificateId}")
  public ResponseEntity<RevokeCertificateDTO> getRevokeCertificateById(
      @PathVariable final String certificateId) {
    return repository
        .findByCertificateId(certificateId)
        .map(revokeCertificateConverter::convert)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(
      summary = "Delete revoke certificate by certificate ID",
      description = "Delete a single revoke certificate by its certificate ID")
  @DeleteMapping("/{certificateId}")
  public void deleteRevokeCertificateById(@PathVariable final String certificateId) {
    repository.deleteById(certificateId);
  }

  @Operation(
      summary = "Get revoke certificates by logical address",
      description = "Retrieve all revoke certificates for a given logical address")
  @GetMapping("/logical-address/{logicalAddress}")
  public List<RevokeCertificateDTO> getRevokeCertificatesByLogicalAddress(
      @PathVariable final String logicalAddress) {
    return repository.findByLogicalAddress(logicalAddress).stream()
        .map(revokeCertificateConverter::convert)
        .toList();
  }

  @Operation(
      summary = "Get revoke certificates by person ID",
      description = "Retrieve all revoke certificates for a given person ID (hyphens ignored)")
  @GetMapping("/person/{personId}")
  public List<RevokeCertificateDTO> getRevokeCertificatesByPersonId(
      @PathVariable final String personId) {
    return repository.findByPersonId(personId.replace("-", "")).stream()
        .map(revokeCertificateConverter::convert)
        .toList();
  }
}
