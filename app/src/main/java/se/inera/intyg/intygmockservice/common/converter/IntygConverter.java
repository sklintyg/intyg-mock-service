package se.inera.intyg.intygmockservice.common.converter;

import jakarta.xml.bind.JAXBElement;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.common.dto.CodeTypeDTO;
import se.inera.intyg.intygmockservice.common.dto.IntygDTO;
import se.inera.intyg.intygmockservice.common.dto.IntygDTO.RelationDTO;
import se.inera.intyg.intygmockservice.common.dto.IntygDTO.SvarDTO;
import se.inera.intyg.intygmockservice.common.dto.IntygDTO.SvarDTO.DelsvarDTO;
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
        final var intyg = new IntygDTO();

        intyg.setIntygsId(intygIdConverter.convert(source.getIntygsId()));
        intyg.setTyp(convertTyp(source.getTyp()));
        intyg.setVersion(source.getVersion());
        intyg.setSigneringstidpunkt(source.getSigneringstidpunkt());
        intyg.setSkickatTidpunkt(source.getSkickatTidpunkt());
        intyg.setPatient(patientConverter.convert(source.getPatient()));
        intyg.setSkapadAv(hosPersonalConverter.convert(source.getSkapadAv()));
        intyg.setRelation(
            Stream.ofNullable(source.getRelation()).flatMap(List::stream)
                .map(this::convertRelation)
                .toList()
        );
        intyg.setSvar(convertSvarList(source.getSvar()));

        return intyg;
    }

    public RelationDTO convertRelation(Relation source) {
        final var target = new RelationDTO();
        target.setTyp(convertTyp(source.getTyp()));
        target.setIntygsId(intygIdConverter.convert(source.getIntygsId()));
        return target;
    }

    private CodeTypeDTO convertTyp(TypAvIntyg source) {
        final var target = new CodeTypeDTO();
        target.setCode(source.getCode());
        target.setCodeSystem(source.getCodeSystem());
        target.setDisplayName(source.getDisplayName());
        return target;
    }

    private CodeTypeDTO convertTyp(TypAvRelation source) {
        final var target = new CodeTypeDTO();
        target.setCode(source.getCode());
        target.setCodeSystem(source.getCodeSystem());
        target.setDisplayName(source.getDisplayName());
        return target;
    }

    private List<SvarDTO> convertSvarList(List<Svar> source) {
        return Stream.ofNullable(source).flatMap(List::stream)
            .map(this::convertSvar)
            .toList();
    }

    private SvarDTO convertSvar(Svar source) {
        final var target = new SvarDTO();
        target.setId(source.getId());
        target.setInstans(String.valueOf(source.getInstans()));
        target.setDelsvar(source.getDelsvar().stream().map(this::convertDelsvar).toList());
        return target;
    }

    private DelsvarDTO convertDelsvar(Svar.Delsvar source) {
        final var target = new DelsvarDTO();
        target.setId(source.getId());
        if (source.getContent().size() > 1 && source.getContent().get(1) instanceof JAXBElement<?> jaxbElement) {
            if (jaxbElement.getValue() instanceof CVType cvType) {
                target.setCv(convertCv(cvType));
            } else if (jaxbElement.getValue() instanceof DatePeriodType datePeriodType) {
                target.setDatePeriod(convertDatePeriod(datePeriodType));
            }
        } else if (source.getContent().get(0) instanceof JAXBElement<?> jaxbElement) {
            if (jaxbElement.getValue() instanceof CVType cvType) {
                target.setCv(convertCv(cvType));
            } else if (jaxbElement.getValue() instanceof DatePeriodType datePeriodType) {
                target.setDatePeriod(convertDatePeriod(datePeriodType));
            }
        } else {
            target.setValue((String) source.getContent().getFirst());
        }
        return target;
    }

    private CodeTypeDTO convertCv(CVType source) {
        final var target = new CodeTypeDTO();
        target.setCode(source.getCode());
        target.setCodeSystem(source.getCodeSystem());
        target.setDisplayName(source.getDisplayName());
        return target;
    }

    private DelsvarDTO.DatePeriod convertDatePeriod(DatePeriodType source) {
        final var target = new DelsvarDTO.DatePeriod();
        target.setStart(source.getStart());
        target.setEnd(source.getEnd());
        return target;
    }
}
