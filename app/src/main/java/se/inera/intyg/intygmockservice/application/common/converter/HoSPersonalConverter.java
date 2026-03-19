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
    return HoSPersonalDTO.builder()
        .personalId(convertPersonalId(source.getPersonalId()))
        .fullstandigtNamn(source.getFullstandigtNamn())
        .forskrivarkod(source.getForskrivarkod())
        .befattning(
            Stream.ofNullable(source.getBefattning())
                .flatMap(Collection::stream)
                .map(this::convertBefattning)
                .toList())
        .enhet(convertEnhet(source.getEnhet()))
        .build();
  }

  private PersonalIdDTO convertPersonalId(HsaId source) {
    return PersonalIdDTO.builder().root(source.getRoot()).extension(source.getExtension()).build();
  }

  private CodeTypeDTO convertBefattning(Befattning source) {
    return CodeTypeDTO.builder()
        .code(source.getCode())
        .codeSystem(source.getCodeSystem())
        .displayName(source.getDisplayName())
        .build();
  }

  private EnhetDTO convertEnhet(Enhet source) {
    return EnhetDTO.builder()
        .enhetsId(convertHsaId(source.getEnhetsId()))
        .arbetsplatskod(convertArbetsplatskod(source.getArbetsplatskod()))
        .enhetsnamn(source.getEnhetsnamn())
        .postadress(source.getPostadress())
        .postnummer(source.getPostnummer())
        .postort(source.getPostort())
        .telefonnummer(source.getTelefonnummer())
        .epost(source.getEpost())
        .vardgivare(convertVardgivare(source.getVardgivare()))
        .build();
  }

  private HsaIdDTO convertHsaId(HsaId source) {
    return HsaIdDTO.builder().root(source.getRoot()).extension(source.getExtension()).build();
  }

  private ArbetsplatskodDTO convertArbetsplatskod(ArbetsplatsKod source) {
    return ArbetsplatskodDTO.builder()
        .root(source.getRoot())
        .extension(source.getExtension())
        .build();
  }

  private VardgivareDTO convertVardgivare(Vardgivare source) {
    return VardgivareDTO.builder()
        .vardgivareId(convertHsaId(source.getVardgivareId()))
        .vardgivarnamn(source.getVardgivarnamn())
        .build();
  }
}
