package se.inera.intyg.intygmockservice.statusupdates.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.common.converter.IntygConverter;
import se.inera.intyg.intygmockservice.statusupdates.dto.CertificateStatusUpdateForCareDTO;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareType;

@Component
@RequiredArgsConstructor
public class CertificateStatusUpdateForCareConverter {

    private final IntygConverter intygConverter;

    public CertificateStatusUpdateForCareDTO convert(CertificateStatusUpdateForCareType source) {
        final var target = new CertificateStatusUpdateForCareDTO();

        // Convert Intyg
        target.setIntyg(intygConverter.convert(source.getIntyg()));

        // Convert Handelse
        final var handelse = new CertificateStatusUpdateForCareDTO.Handelse();
        handelse.setHandelsekod(convertHandelsekod(source.getHandelse().getHandelsekod()));
        handelse.setTidpunkt(source.getHandelse().getTidpunkt().toString());
        target.setHandelse(handelse);

        // Convert Fragor
        target.setSkickadeFragor(convertFragor(source.getSkickadeFragor()));
        target.setMottagnaFragor(convertFragor(source.getMottagnaFragor()));

        // Convert HanteratAv
        if (source.getHanteratAv() != null) {
            final var hanteratAv = new CertificateStatusUpdateForCareDTO.HanteratAv();
            hanteratAv.setRoot(source.getHanteratAv().getRoot());
            hanteratAv.setExtension(source.getHanteratAv().getExtension());
            target.setHanteratAv(hanteratAv);
        }

        return target;
    }

    private CertificateStatusUpdateForCareDTO.Handelse.Handelsekod convertHandelsekod(
        se.riv.clinicalprocess.healthcond.certificate.types.v3.Handelsekod source) {
        final var target = new CertificateStatusUpdateForCareDTO.Handelse.Handelsekod();
        target.setCode(source.getCode());
        target.setCodeSystem(source.getCodeSystem());
        target.setDisplayName(source.getDisplayName());
        return target;
    }

    private CertificateStatusUpdateForCareDTO.Fragor convertFragor(
        se.riv.clinicalprocess.healthcond.certificate.v3.Arenden source) {
        final var target = new CertificateStatusUpdateForCareDTO.Fragor();
        target.setTotalt(source.getTotalt());
        target.setEjBesvarade(source.getEjBesvarade());
        target.setBesvarade(source.getBesvarade());
        target.setHanterade(source.getHanterade());
        return target;
    }
}