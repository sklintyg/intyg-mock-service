package se.inera.intyg.intygmockservice.statusupdates;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareResponderInterface;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareResponseType;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@Service
@RequiredArgsConstructor
public class CertificateStatusUpdateForCareResponderImpl
    implements CertificateStatusUpdateForCareResponderInterface {

  private final CertificateStatusUpdateForCareService service;

  @Override
  public CertificateStatusUpdateForCareResponseType certificateStatusUpdateForCare(
      String logicalAddress,
      CertificateStatusUpdateForCareType certificateStatusUpdateForCareType) {

    service.store(logicalAddress, certificateStatusUpdateForCareType);

    final var response = new CertificateStatusUpdateForCareResponseType();
    final var result = new ResultType();
    response.setResult(result);
    result.setResultCode(ResultCodeType.OK);

    return response;
  }
}
