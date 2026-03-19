package se.inera.intyg.intygmockservice.application.common.converter;

import jakarta.xml.bind.JAXBElement;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.application.common.dto.CodeTypeDTO;
import se.inera.intyg.intygmockservice.application.common.dto.IntygDTO;
import se.inera.intyg.intygmockservice.application.common.dto.IntygDTO.RelationDTO;
import se.inera.intyg.intygmockservice.application.common.dto.IntygDTO.SvarDTO;
import se.inera.intyg.intygmockservice.application.common.dto.IntygDTO.SvarDTO.DelsvarDTO;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.CVType;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.DatePeriodType;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.TypAvIntyg;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.TypAvRelation;
import se.riv.clinicalprocess.healthcond.certificate.v3.Intyg;
import se.riv.clinicalprocess.healthcond.certificate.v3.Relation;
import se.riv.clinicalprocess.healthcond.certificate.v3.Svar;

@Component
@RequiredArgsConstructor
public class IntygConverter {

  private final HoSPersonalConverter hosPersonalConverter;
  private final PatientConverter patientConverter;
  private final IntygIdConverter intygIdConverter;

  public IntygDTO convert(Intyg source) {
    return IntygDTO.builder()
        .intygsId(intygIdConverter.convert(source.getIntygsId()))
        .typ(convertTyp(source.getTyp()))
        .version(source.getVersion())
        .signeringstidpunkt(source.getSigneringstidpunkt())
        .skickatTidpunkt(source.getSkickatTidpunkt())
        .patient(patientConverter.convert(source.getPatient()))
        .skapadAv(hosPersonalConverter.convert(source.getSkapadAv()))
        .relation(
            Stream.ofNullable(source.getRelation())
                .flatMap(List::stream)
                .map(this::convertRelation)
                .toList())
        .svar(convertSvarList(source.getSvar()))
        .build();
  }

  public RelationDTO convertRelation(Relation source) {
    return RelationDTO.builder()
        .typ(convertTyp(source.getTyp()))
        .intygsId(intygIdConverter.convert(source.getIntygsId()))
        .build();
  }

  private CodeTypeDTO convertTyp(TypAvIntyg source) {
    return CodeTypeDTO.builder()
        .code(source.getCode())
        .codeSystem(source.getCodeSystem())
        .displayName(source.getDisplayName())
        .build();
  }

  private CodeTypeDTO convertTyp(TypAvRelation source) {
    return CodeTypeDTO.builder()
        .code(source.getCode())
        .codeSystem(source.getCodeSystem())
        .displayName(source.getDisplayName())
        .build();
  }

  private List<SvarDTO> convertSvarList(List<Svar> source) {
    return Stream.ofNullable(source).flatMap(List::stream).map(this::convertSvar).toList();
  }

  private SvarDTO convertSvar(Svar source) {
    return SvarDTO.builder()
        .id(source.getId())
        .instans(String.valueOf(source.getInstans()))
        .delsvar(source.getDelsvar().stream().map(this::convertDelsvar).toList())
        .build();
  }

  private DelsvarDTO convertDelsvar(Svar.Delsvar source) {
    final var builder = DelsvarDTO.builder().id(source.getId());
    if (source.getContent().size() > 1
        && source.getContent().get(1) instanceof JAXBElement<?> jaxbElement) {
      if (jaxbElement.getValue() instanceof CVType cvType) {
        builder.cv(convertCv(cvType));
      } else if (jaxbElement.getValue() instanceof DatePeriodType datePeriodType) {
        builder.datePeriod(convertDatePeriod(datePeriodType));
      }
    } else if (source.getContent().get(0) instanceof JAXBElement<?> jaxbElement) {
      if (jaxbElement.getValue() instanceof CVType cvType) {
        builder.cv(convertCv(cvType));
      } else if (jaxbElement.getValue() instanceof DatePeriodType datePeriodType) {
        builder.datePeriod(convertDatePeriod(datePeriodType));
      }
    } else {
      builder.value((String) source.getContent().getFirst());
    }
    return builder.build();
  }

  private CodeTypeDTO convertCv(CVType source) {
    return CodeTypeDTO.builder()
        .code(source.getCode())
        .codeSystem(source.getCodeSystem())
        .displayName(source.getDisplayName())
        .build();
  }

  private DelsvarDTO.DatePeriod convertDatePeriod(DatePeriodType source) {
    return DelsvarDTO.DatePeriod.builder().start(source.getStart()).end(source.getEnd()).build();
  }
}
