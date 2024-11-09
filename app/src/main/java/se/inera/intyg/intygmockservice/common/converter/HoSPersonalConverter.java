package se.inera.intyg.intygmockservice.common.converter;

import java.util.Collection;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.common.dto.CodeTypeDTO;
import se.inera.intyg.intygmockservice.common.dto.HoSPersonalDTO;
import se.inera.intyg.intygmockservice.common.dto.HoSPersonalDTO.EnhetDTO;
import se.inera.intyg.intygmockservice.common.dto.HoSPersonalDTO.EnhetDTO.ArbetsplatskodDTO;
import se.inera.intyg.intygmockservice.common.dto.HoSPersonalDTO.EnhetDTO.HsaIdDTO;
import se.inera.intyg.intygmockservice.common.dto.HoSPersonalDTO.EnhetDTO.VardgivareDTO;
import se.inera.intyg.intygmockservice.common.dto.HoSPersonalDTO.PersonalIdDTO;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.ArbetsplatsKod;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.Befattning;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.HsaId;
import se.riv.clinicalprocess.healthcond.certificate.v3.Enhet;
import se.riv.clinicalprocess.healthcond.certificate.v3.HosPersonal;
import se.riv.clinicalprocess.healthcond.certificate.v3.Vardgivare;

@Component
public class HoSPersonalConverter {

    public HoSPersonalDTO convert(HosPersonal source) {
        final var target = new HoSPersonalDTO();
        target.setPersonalId(convertPersonalId(source.getPersonalId()));
        target.setFullstandigtNamn(source.getFullstandigtNamn());
        target.setForskrivarkod(source.getForskrivarkod());
        target.setBefattning(
            Stream.ofNullable(source.getBefattning())
                .flatMap(Collection::stream)
                .map(this::convertBefattning)
                .toList()
        );
        target.setEnhet(convertEnhet(source.getEnhet()));
        return target;
    }

    private PersonalIdDTO convertPersonalId(HsaId source) {
        final var target = new PersonalIdDTO();
        target.setRoot(source.getRoot());
        target.setExtension(source.getExtension());
        return target;
    }

    private CodeTypeDTO convertBefattning(Befattning source) {
        final var target = new CodeTypeDTO();
        target.setCode(source.getCode());
        target.setCodeSystem(source.getCodeSystem());
        target.setDisplayName(source.getDisplayName());
        return target;
    }

    private EnhetDTO convertEnhet(Enhet source) {
        final var target = new EnhetDTO();
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

    private HsaIdDTO convertEnhetsId(HsaId source) {
        final var target = new HsaIdDTO();
        target.setRoot(source.getRoot());
        target.setExtension(source.getExtension());
        return target;
    }

    private ArbetsplatskodDTO convertArbetsplatskod(ArbetsplatsKod source) {
        final var target = new ArbetsplatskodDTO();
        target.setRoot(source.getRoot());
        target.setExtension(source.getExtension());
        return target;
    }

    private VardgivareDTO convertVardgivare(Vardgivare source) {
        final var target = new VardgivareDTO();
        target.setVardgivareId(convertVardgivareId(source.getVardgivareId()));
        target.setVardgivarnamn(source.getVardgivarnamn());
        return target;
    }

    private HsaIdDTO convertVardgivareId(HsaId source) {
        final var target = new HsaIdDTO();
        target.setRoot(source.getRoot());
        target.setExtension(source.getExtension());
        return target;
    }
}
