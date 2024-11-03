package se.inera.intyg.intygmockservice.statusupdates;

import jakarta.jws.WebService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.statusupdates.converter.CertificateStatusUpdateForCareConverter;
import se.inera.intyg.intygmockservice.statusupdates.repository.CertificateStatusUpdateForCareRepository;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareResponderInterface;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareResponseType;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@WebService
@Component
@Slf4j
@RequiredArgsConstructor
public class CertificateStatusUpdateForCareResponderImpl implements
    CertificateStatusUpdateForCareResponderInterface {

  private final CertificateStatusUpdateForCareRepository repository;
  private final CertificateStatusUpdateForCareConverter converter;

  @Override
  public CertificateStatusUpdateForCareResponseType certificateStatusUpdateForCare(
      String logicalAddress,
      CertificateStatusUpdateForCareType certificateStatusUpdateForCareType) {

    repository.add(logicalAddress, certificateStatusUpdateForCareType);

    final var response = new CertificateStatusUpdateForCareResponseType();
    final var result = new ResultType();
    response.setResult(result);
    result.setResultCode(ResultCodeType.OK);

    final var statusUpdateForCareDTO = converter.convert(certificateStatusUpdateForCareType);

    log.atInfo().setMessage("Certificate for care status update received")
        .addKeyValue("event.logical_address", logicalAddress)
        .addKeyValue("event.certificate.id",
            statusUpdateForCareDTO.getIntyg().getIntygsId().getExtension()
        )
        .addKeyValue("event.type", statusUpdateForCareDTO.getHandelse().getHandelsekod().getCode())
        .addKeyValue("event.handled_by", statusUpdateForCareDTO.getHanteratAv() != null
            ? statusUpdateForCareDTO.getHanteratAv().getExtension()
            : null
        )
        .log();

    return response;
  }
}
