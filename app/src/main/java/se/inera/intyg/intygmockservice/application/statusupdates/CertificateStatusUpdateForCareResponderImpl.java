package se.inera.intyg.intygmockservice.application.statusupdates;

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
    return service
        .store(logicalAddress, certificateStatusUpdateForCareType)
        .orElseGet(
            () -> {
              final var response = new CertificateStatusUpdateForCareResponseType();
              final var result = new ResultType();
              result.setResultCode(ResultCodeType.OK);
              response.setResult(result);
              return response;
            });
  }
}
