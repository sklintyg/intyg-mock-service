package se.inera.intyg.intygmockservice.revokecertificate.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.common.repository.AbstractInMemoryRepository;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateType;

@Repository
public class RevokeCertificateRepository extends AbstractInMemoryRepository<RevokeCertificateType> {

  public RevokeCertificateRepository(
      @Value("${app.repository.revoke-certificate.max-size:1000}") int maxSize) {
    super(maxSize);
  }
}
