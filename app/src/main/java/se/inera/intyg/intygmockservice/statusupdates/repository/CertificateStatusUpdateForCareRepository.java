package se.inera.intyg.intygmockservice.statusupdates.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.common.repository.AbstractInMemoryRepository;
import se.inera.intyg.intygmockservice.config.properties.AppProperties;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareType;

@Repository
public class CertificateStatusUpdateForCareRepository
    extends AbstractInMemoryRepository<CertificateStatusUpdateForCareType> {

  public CertificateStatusUpdateForCareRepository(AppProperties appProperties) {
    super(appProperties.repository().certificateStatusUpdateForCare().maxSize());
  }

  public List<CertificateStatusUpdateForCareType> findByCertificateId(final String certificateId) {
    return findAll().stream()
        .filter(t -> certificateId.equals(t.getIntyg().getIntygsId().getExtension()))
        .toList();
  }

  public List<CertificateStatusUpdateForCareType> findByLogicalAddress(
      final String logicalAddress) {
    return findByKey(logicalAddress);
  }

  public List<CertificateStatusUpdateForCareType> findByPersonId(final String normalizedPersonId) {
    return findAll().stream()
        .filter(
            t ->
                normalizedPersonId.equals(
                    normalize(t.getIntyg().getPatient().getPersonId().getExtension())))
        .toList();
  }

  public List<CertificateStatusUpdateForCareType> findByEventCode(final String eventCode) {
    return findAll().stream()
        .filter(t -> eventCode.equals(t.getHandelse().getHandelsekod().getCode()))
        .toList();
  }

  public void deleteByCertificateId(final String certificateId) {
    removeIf(t -> certificateId.equals(t.getIntyg().getIntygsId().getExtension()));
  }

  private static String normalize(final String personId) {
    return personId == null ? null : personId.replace("-", "");
  }
}
