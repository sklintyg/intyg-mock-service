package se.inera.intyg.intygmockservice.common.converter;

import jakarta.xml.bind.JAXBElement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.common.dto.IntygDTO;
import se.inera.intyg.intygmockservice.common.dto.IntygDTO.Svar;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.CVType;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.DatePeriodType;
import se.riv.clinicalprocess.healthcond.certificate.v3.Intyg;

@Component
@RequiredArgsConstructor
public class IntygConverter {

    private final HoSPersonalConverter hosPersonalConverter;
    private final PatientConverter patientConverter;
    private final IntygIdConverter intygIdConverter;

    public IntygDTO convert(Intyg source) {
        IntygDTO intyg = new IntygDTO();

        intyg.setIntygsId(intygIdConverter.convert(source.getIntygsId()));
        intyg.setTyp(convertTyp(source.getTyp()));
        intyg.setVersion(source.getVersion());
        intyg.setSigneringstidpunkt(source.getSigneringstidpunkt());
        intyg.setSkickatTidpunkt(source.getSkickatTidpunkt());
        intyg.setPatient(patientConverter.convert(source.getPatient()));
        intyg.setSkapadAv(hosPersonalConverter.convert(source.getSkapadAv()));
        intyg.setRelation(
            source.getRelation().stream().map(this::convertRelation).toList()
        );
        intyg.setSvar(convertSvarList(source.getSvar()));

        return intyg;
    }

    public IntygDTO.Relation convertRelation(se.riv.clinicalprocess.healthcond.certificate.v3.Relation source) {
        if (source == null) {
            return null;
        }
        IntygDTO.Relation target = new IntygDTO.Relation();
        target.setTyp(convertTyp(source.getTyp()));
        target.setIntygsId(intygIdConverter.convert(source.getIntygsId()));
        return target;
    }

    private IntygDTO.Typ convertTyp(
        se.riv.clinicalprocess.healthcond.certificate.types.v3.TypAvIntyg source) {
        IntygDTO.Typ target = new IntygDTO.Typ();
        target.setCode(source.getCode());
        target.setCodeSystem(source.getCodeSystem());
        target.setDisplayName(source.getDisplayName());
        return target;
    }

    private IntygDTO.Relation.TypAvRelation convertTyp(
        se.riv.clinicalprocess.healthcond.certificate.types.v3.TypAvRelation source) {
        IntygDTO.Relation.TypAvRelation target = new IntygDTO.Relation.TypAvRelation();
        target.setCode(source.getCode());
        target.setCodeSystem(source.getCodeSystem());
        target.setDisplayName(source.getDisplayName());
        return target;
    }

    private List<Svar> convertSvarList(List<se.riv.clinicalprocess.healthcond.certificate.v3.Svar> source) {
        return source.stream().map(this::convertSvar).toList();
    }

    private IntygDTO.Svar convertSvar(se.riv.clinicalprocess.healthcond.certificate.v3.Svar source) {
        IntygDTO.Svar target = new IntygDTO.Svar();
        target.setId(source.getId());
        target.setInstans(String.valueOf(source.getInstans()));
        target.setDelsvar(source.getDelsvar().stream().map(this::convertDelsvar).toList());
        return target;
    }

    private IntygDTO.Svar.Delsvar convertDelsvar(se.riv.clinicalprocess.healthcond.certificate.v3.Svar.Delsvar source) {
        IntygDTO.Svar.Delsvar target = new IntygDTO.Svar.Delsvar();
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

    private IntygDTO.Svar.Delsvar.Cv convertCv(CVType source) {
        IntygDTO.Svar.Delsvar.Cv target = new IntygDTO.Svar.Delsvar.Cv();
        target.setCode(source.getCode());
        target.setCodeSystem(source.getCodeSystem());
        target.setDisplayName(source.getDisplayName());
        return target;
    }

    private IntygDTO.Svar.Delsvar.DatePeriod convertDatePeriod(DatePeriodType source) {
        IntygDTO.Svar.Delsvar.DatePeriod target = new IntygDTO.Svar.Delsvar.DatePeriod();
        target.setStart(source.getStart());
        target.setEnd(source.getEnd());
        return target;
    }
}
