package se.inera.intyg.intygmockservice.statusupdates.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareType;

@Repository
public class CertificateStatusUpdateForCareRepository {

  private final Map<String, List<CertificateStatusUpdateForCareType>> repository = new HashMap<>();

  public void add(String logicalAddress,
      CertificateStatusUpdateForCareType certificateStatusUpdateForCareType) {
    repository.computeIfAbsent(logicalAddress, k -> new ArrayList<>())
        .add(certificateStatusUpdateForCareType);
  }

  public List<CertificateStatusUpdateForCareType> findAll() {
    return repository.values().stream()
        .flatMap(List::stream)
        .toList();
  }

  public void deleteAll() {
    repository.clear();
  }
}