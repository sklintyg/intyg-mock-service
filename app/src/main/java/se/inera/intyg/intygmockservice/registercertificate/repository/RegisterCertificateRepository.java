package se.inera.intyg.intygmockservice.registercertificate.repository;

import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.common.repository.AbstractInMemoryRepository;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;

@Repository
public class RegisterCertificateRepository extends AbstractInMemoryRepository<RegisterCertificateType> {

}