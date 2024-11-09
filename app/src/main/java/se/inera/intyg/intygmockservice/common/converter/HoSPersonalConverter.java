package se.inera.intyg.intygmockservice.common.converter;

import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.common.dto.HoSPersonalDTO;

@Component
public class HoSPersonalConverter {

    public HoSPersonalDTO convert(se.riv.clinicalprocess.healthcond.certificate.v3.HosPersonal source) {
        HoSPersonalDTO target = new HoSPersonalDTO();
        target.setPersonalId(convertPersonalId(source.getPersonalId()));
        target.setFullstandigtNamn(source.getFullstandigtNamn());
        target.setForskrivarkod(source.getForskrivarkod());
        target.setBefattning(
            source.getBefattning().stream().map(this::convertBefattning).findFirst().orElse(null)
        );
        target.setEnhet(convertEnhet(source.getEnhet()));
        return target;
    }

    private HoSPersonalDTO.PersonalId convertPersonalId(
        se.riv.clinicalprocess.healthcond.certificate.types.v3.HsaId source) {
        HoSPersonalDTO.PersonalId target = new HoSPersonalDTO.PersonalId();
        target.setRoot(source.getRoot());
        target.setExtension(source.getExtension());
        return target;
    }

    private HoSPersonalDTO.Befattning convertBefattning(
        se.riv.clinicalprocess.healthcond.certificate.types.v3.Befattning source) {
        HoSPersonalDTO.Befattning target = new HoSPersonalDTO.Befattning();
        target.setCode(source.getCode());
        target.setCodeSystem(source.getCodeSystem());
        target.setDisplayName(source.getDisplayName());
        return target;
    }

    private HoSPersonalDTO.Enhet convertEnhet(
        se.riv.clinicalprocess.healthcond.certificate.v3.Enhet source) {
        HoSPersonalDTO.Enhet target = new HoSPersonalDTO.Enhet();
        target.setEnhetsId(convertEnhetsId(source.getEnhetsId()));
        target.setArbetsplatskod(convertArbetsplatskod(source.getArbetsplatskod()));
        target.setEnhetsnamn(source.getEnhetsnamn());
        target.setPostadress(source.getPostadress());
        target.setPostnummer(source.getPostnummer());
        target.setPostort(source.getPostort());
        target.setTelefonnummer(source.getTelefonnummer());
        target.setEpost(source.getEpost());
        target.setVardgivare(convertVardgivare(source.getVardgivare()));
        return target;
    }

    private HoSPersonalDTO.Enhet.EnhetsId convertEnhetsId(
        se.riv.clinicalprocess.healthcond.certificate.types.v3.HsaId source) {
        HoSPersonalDTO.Enhet.EnhetsId target = new HoSPersonalDTO.Enhet.EnhetsId();
        target.setRoot(source.getRoot());
        target.setExtension(source.getExtension());
        return target;
    }

    private HoSPersonalDTO.Enhet.Arbetsplatskod convertArbetsplatskod(
        se.riv.clinicalprocess.healthcond.certificate.types.v3.ArbetsplatsKod source) {
        HoSPersonalDTO.Enhet.Arbetsplatskod target = new HoSPersonalDTO.Enhet.Arbetsplatskod();
        target.setRoot(source.getRoot());
        target.setExtension(source.getExtension());
        return target;
    }

    private HoSPersonalDTO.Enhet.Vardgivare convertVardgivare(
        se.riv.clinicalprocess.healthcond.certificate.v3.Vardgivare source) {
        HoSPersonalDTO.Enhet.Vardgivare target = new HoSPersonalDTO.Enhet.Vardgivare();
        target.setVardgivareId(convertVardgivareId(source.getVardgivareId()));
        target.setVardgivarnamn(source.getVardgivarnamn());
        return target;
    }

    private HoSPersonalDTO.Enhet.Vardgivare.VardgivareId convertVardgivareId(
        se.riv.clinicalprocess.healthcond.certificate.types.v3.HsaId source) {
        HoSPersonalDTO.Enhet.Vardgivare.VardgivareId target = new HoSPersonalDTO.Enhet.Vardgivare.VardgivareId();
        target.setRoot(source.getRoot());
        target.setExtension(source.getExtension());
        return target;
    }
}
