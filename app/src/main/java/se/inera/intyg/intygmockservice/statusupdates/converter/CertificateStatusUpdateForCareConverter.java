package se.inera.intyg.intygmockservice.statusupdates.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.common.converter.IntygConverter;
import se.inera.intyg.intygmockservice.statusupdates.dto.CertificateStatusUpdateForCareDTO;
import se.inera.intyg.intygmockservice.statusupdates.dto.CertificateStatusUpdateForCareDTO.Fragor;
import se.inera.intyg.intygmockservice.statusupdates.dto.CertificateStatusUpdateForCareDTO.Handelse;
import se.inera.intyg.intygmockservice.statusupdates.dto.CertificateStatusUpdateForCareDTO.Handelse.Handelsekod;
import se.inera.intyg.intygmockservice.statusupdates.dto.CertificateStatusUpdateForCareDTO.HanteratAv;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareType;
import se.riv.clinicalprocess.healthcond.certificate.v3.Arenden;

@Component
@RequiredArgsConstructor
public class CertificateStatusUpdateForCareConverter {

  private final IntygConverter intygConverter;

  public CertificateStatusUpdateForCareDTO convert(CertificateStatusUpdateForCareType source) {
    final var sourceHandelse = source.getHandelse();
    final var builder =
        CertificateStatusUpdateForCareDTO.builder()
            .intyg(intygConverter.convert(source.getIntyg()))
            .handelse(
                Handelse.builder()
                    .handelsekod(
                        Handelsekod.builder()
                            .code(sourceHandelse.getHandelsekod().getCode())
                            .codeSystem(sourceHandelse.getHandelsekod().getCodeSystem())
                            .displayName(sourceHandelse.getHandelsekod().getDisplayName())
                            .build())
                    .tidpunkt(sourceHandelse.getTidpunkt().toString())
                    .build())
            .skickadeFragor(convertFragor(source.getSkickadeFragor()))
            .mottagnaFragor(convertFragor(source.getMottagnaFragor()));

    if (source.getHanteratAv() != null) {
      builder.hanteratAv(
          HanteratAv.builder()
              .root(source.getHanteratAv().getRoot())
              .extension(source.getHanteratAv().getExtension())
              .build());
    }

    return builder.build();
  }

  private Fragor convertFragor(Arenden source) {
    return Fragor.builder()
        .totalt(source.getTotalt())
        .ejBesvarade(source.getEjBesvarade())
        .besvarade(source.getBesvarade())
        .hanterade(source.getHanterade())
        .build();
  }
}
