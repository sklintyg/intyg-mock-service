package se.inera.intyg.intygmockservice.revokecertificate.repository;

import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.common.repository.AbstractInMemoryRepository;
import se.inera.intyg.intygmockservice.config.properties.AppProperties;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateType;

@Repository
public class RevokeCertificateRepository extends AbstractInMemoryRepository<RevokeCertificateType> {

  public RevokeCertificateRepository(AppProperties appProperties) {
    super(appProperties.repository().revokeCertificate().maxSize());
  }
}
