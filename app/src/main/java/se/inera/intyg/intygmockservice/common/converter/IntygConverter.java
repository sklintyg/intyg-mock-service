package se.inera.intyg.intygmockservice.common.converter;

import jakarta.xml.bind.JAXBElement;
import java.util.List;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.common.dto.IntygDTO;
import se.inera.intyg.intygmockservice.common.dto.IntygDTO.Svar;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.CVType;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.DatePeriodType;
import se.riv.clinicalprocess.healthcond.certificate.v3.Intyg;

@Component
public class IntygConverter {

    public IntygDTO convert(Intyg source) {
        IntygDTO intyg = new IntygDTO();

        intyg.setIntygsId(convertIntygsId(source.getIntygsId()));
        intyg.setTyp(convertTyp(source.getTyp()));
        intyg.setVersion(source.getVersion());
        intyg.setSigneringstidpunkt(source.getSigneringstidpunkt());
        intyg.setSkickatTidpunkt(source.getSkickatTidpunkt());
        intyg.setPatient(convertPatient(source.getPatient()));
        intyg.setSkapadAv(convertSkapadAv(source.getSkapadAv()));
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
        target.setIntygsId(convertIntygsId(source.getIntygsId()));
        return target;
    }

    private IntygDTO.IntygsId convertIntygsId(
        se.riv.clinicalprocess.healthcond.certificate.types.v3.IntygId source) {
        IntygDTO.IntygsId target = new IntygDTO.IntygsId();
        target.setRoot(source.getRoot());
        target.setExtension(source.getExtension());
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

    private IntygDTO.Patient convertPatient(
        se.riv.clinicalprocess.healthcond.certificate.v3.Patient source) {
        IntygDTO.Patient target = new IntygDTO.Patient();
        target.setPersonId(convertPersonId(source.getPersonId()));
        target.setFornamn(source.getFornamn());
        target.setEfternamn(source.getEfternamn());
        target.setPostadress(source.getPostadress());
        target.setPostnummer(source.getPostnummer());
        target.setPostort(source.getPostort());
        return target;
    }

    private IntygDTO.Patient.PersonId convertPersonId(
        se.riv.clinicalprocess.healthcond.certificate.types.v3.PersonId source) {
        IntygDTO.Patient.PersonId target = new IntygDTO.Patient.PersonId();
        target.setRoot(source.getRoot());
        target.setExtension(source.getExtension());
        return target;
    }

    private IntygDTO.SkapadAv convertSkapadAv(
        se.riv.clinicalprocess.healthcond.certificate.v3.HosPersonal source) {
        IntygDTO.SkapadAv target = new IntygDTO.SkapadAv();
        target.setPersonalId(convertPersonalId(source.getPersonalId()));
        target.setFullstandigtNamn(source.getFullstandigtNamn());
        target.setForskrivarkod(source.getForskrivarkod());
        target.setBefattning(
            source.getBefattning().stream().map(this::convertBefattning).findFirst().orElse(null)
        );
        target.setEnhet(convertEnhet(source.getEnhet()));
        return target;
    }

    private IntygDTO.SkapadAv.PersonalId convertPersonalId(
        se.riv.clinicalprocess.healthcond.certificate.types.v3.HsaId source) {
        IntygDTO.SkapadAv.PersonalId target = new IntygDTO.SkapadAv.PersonalId();
        target.setRoot(source.getRoot());
        target.setExtension(source.getExtension());
        return target;
    }

    private IntygDTO.SkapadAv.Befattning convertBefattning(
        se.riv.clinicalprocess.healthcond.certificate.types.v3.Befattning source) {
        IntygDTO.SkapadAv.Befattning target = new IntygDTO.SkapadAv.Befattning();
        target.setCode(source.getCode());
        target.setCodeSystem(source.getCodeSystem());
        target.setDisplayName(source.getDisplayName());
        return target;
    }

    private IntygDTO.SkapadAv.Enhet convertEnhet(
        se.riv.clinicalprocess.healthcond.certificate.v3.Enhet source) {
        IntygDTO.SkapadAv.Enhet target = new IntygDTO.SkapadAv.Enhet();
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

    private IntygDTO.SkapadAv.Enhet.EnhetsId convertEnhetsId(
        se.riv.clinicalprocess.healthcond.certificate.types.v3.HsaId source) {
        IntygDTO.SkapadAv.Enhet.EnhetsId target = new IntygDTO.SkapadAv.Enhet.EnhetsId();
        target.setRoot(source.getRoot());
        target.setExtension(source.getExtension());
        return target;
    }

    private IntygDTO.SkapadAv.Enhet.Arbetsplatskod convertArbetsplatskod(
        se.riv.clinicalprocess.healthcond.certificate.types.v3.ArbetsplatsKod source) {
        IntygDTO.SkapadAv.Enhet.Arbetsplatskod target = new IntygDTO.SkapadAv.Enhet.Arbetsplatskod();
        target.setRoot(source.getRoot());
        target.setExtension(source.getExtension());
        return target;
    }

    private IntygDTO.SkapadAv.Enhet.Vardgivare convertVardgivare(
        se.riv.clinicalprocess.healthcond.certificate.v3.Vardgivare source) {
        IntygDTO.SkapadAv.Enhet.Vardgivare target = new IntygDTO.SkapadAv.Enhet.Vardgivare();
        target.setVardgivareId(convertVardgivareId(source.getVardgivareId()));
        target.setVardgivarnamn(source.getVardgivarnamn());
        return target;
    }

    private IntygDTO.SkapadAv.Enhet.Vardgivare.VardgivareId convertVardgivareId(
        se.riv.clinicalprocess.healthcond.certificate.types.v3.HsaId source) {
        IntygDTO.SkapadAv.Enhet.Vardgivare.VardgivareId target = new IntygDTO.SkapadAv.Enhet.Vardgivare.VardgivareId();
        target.setRoot(source.getRoot());
        target.setExtension(source.getExtension());
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
