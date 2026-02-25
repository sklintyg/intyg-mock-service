package se.inera.intyg.intygmockservice.registercertificate.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.common.repository.AbstractInMemoryRepository;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;

@Repository
public class RegisterCertificateRepository extends AbstractInMemoryRepository<RegisterCertificateType> {

    public RegisterCertificateRepository(
            @Value("${app.repository.register-certificate.max-size:1000}") int maxSize) {
        super(maxSize);
    }
}
