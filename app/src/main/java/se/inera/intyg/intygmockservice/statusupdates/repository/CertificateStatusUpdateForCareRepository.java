package se.inera.intyg.intygmockservice.statusupdates.repository;

import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.common.repository.AbstractInMemoryRepository;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareType;

@Repository
public class CertificateStatusUpdateForCareRepository extends AbstractInMemoryRepository<CertificateStatusUpdateForCareType> {

}