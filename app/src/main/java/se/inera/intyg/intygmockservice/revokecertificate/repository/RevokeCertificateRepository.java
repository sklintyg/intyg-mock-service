package se.inera.intyg.intygmockservice.revokecertificate.repository;

import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.common.repository.AbstractInMemoryRepository;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateType;

@Repository
public class RevokeCertificateRepository extends AbstractInMemoryRepository<RevokeCertificateType> {

}