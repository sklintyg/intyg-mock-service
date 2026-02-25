package se.inera.intyg.intygmockservice.statusupdates.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.common.repository.AbstractInMemoryRepository;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareType;

@Repository
public class CertificateStatusUpdateForCareRepository extends AbstractInMemoryRepository<CertificateStatusUpdateForCareType> {

    public CertificateStatusUpdateForCareRepository(
        @Value("${app.repository.certificate-status-update-for-care.max-size:1000}") int maxSize) {
        super(maxSize);
    }
}
