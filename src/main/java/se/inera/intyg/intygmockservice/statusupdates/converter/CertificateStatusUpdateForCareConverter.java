package se.inera.intyg.intygmockservice.statusupdates.converter;

import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.statusupdates.dto.CertificateStatusUpdateForCareDTO;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareType;

@Component
public class CertificateStatusUpdateForCareConverter {

  public CertificateStatusUpdateForCareDTO convert(CertificateStatusUpdateForCareType source) {
    CertificateStatusUpdateForCareDTO target = new CertificateStatusUpdateForCareDTO();

    // Convert Intyg
    CertificateStatusUpdateForCareDTO.Intyg intyg = new CertificateStatusUpdateForCareDTO.Intyg();
    intyg.setIntygsId(convertIntygsId(source.getIntyg().getIntygsId()));
    intyg.setTyp(convertTyp(source.getIntyg().getTyp()));
    intyg.setVersion(source.getIntyg().getVersion());
    intyg.setPatient(convertPatient(source.getIntyg().getPatient()));
    intyg.setSkapadAv(convertSkapadAv(source.getIntyg().getSkapadAv()));
    target.setIntyg(intyg);

    // Convert Handelse
    CertificateStatusUpdateForCareDTO.Handelse handelse = new CertificateStatusUpdateForCareDTO.Handelse();
    handelse.setHandelsekod(convertHandelsekod(source.getHandelse().getHandelsekod()));
    handelse.setTidpunkt(source.getHandelse().getTidpunkt().toString());
    target.setHandelse(handelse);

    // Convert Fragor
    target.setSkickadeFragor(convertFragor(source.getSkickadeFragor()));
    target.setMottagnaFragor(convertFragor(source.getMottagnaFragor()));

    // Convert HanteratAv
    CertificateStatusUpdateForCareDTO.HanteratAv hanteratAv = new CertificateStatusUpdateForCareDTO.HanteratAv();
    hanteratAv.setRoot(source.getHanteratAv().getRoot());
    hanteratAv.setExtension(source.getHanteratAv().getExtension());
    target.setHanteratAv(hanteratAv);

    return target;
  }

  private CertificateStatusUpdateForCareDTO.Intyg.IntygsId convertIntygsId(
      se.riv.clinicalprocess.healthcond.certificate.types.v3.IntygId source) {
    CertificateStatusUpdateForCareDTO.Intyg.IntygsId target = new CertificateStatusUpdateForCareDTO.Intyg.IntygsId();
    target.setRoot(source.getRoot());
    target.setExtension(source.getExtension());
    return target;
  }

  private CertificateStatusUpdateForCareDTO.Intyg.Typ convertTyp(
      se.riv.clinicalprocess.healthcond.certificate.types.v3.TypAvIntyg source) {
    CertificateStatusUpdateForCareDTO.Intyg.Typ target = new CertificateStatusUpdateForCareDTO.Intyg.Typ();
    target.setCode(source.getCode());
    target.setCodeSystem(source.getCodeSystem());
    target.setDisplayName(source.getDisplayName());
    return target;
  }

  private CertificateStatusUpdateForCareDTO.Intyg.Patient convertPatient(
      se.riv.clinicalprocess.healthcond.certificate.v3.Patient source) {
    CertificateStatusUpdateForCareDTO.Intyg.Patient target = new CertificateStatusUpdateForCareDTO.Intyg.Patient();
    target.setPersonId(convertPersonId(source.getPersonId()));
    target.setFornamn(source.getFornamn());
    target.setEfternamn(source.getEfternamn());
    target.setPostadress(source.getPostadress());
    target.setPostnummer(source.getPostnummer());
    target.setPostort(source.getPostort());
    return target;
  }

  private CertificateStatusUpdateForCareDTO.Intyg.Patient.PersonId convertPersonId(
      se.riv.clinicalprocess.healthcond.certificate.types.v3.PersonId source) {
    CertificateStatusUpdateForCareDTO.Intyg.Patient.PersonId target = new CertificateStatusUpdateForCareDTO.Intyg.Patient.PersonId();
    target.setRoot(source.getRoot());
    target.setExtension(source.getExtension());
    return target;
  }

  private CertificateStatusUpdateForCareDTO.Intyg.SkapadAv convertSkapadAv(
      se.riv.clinicalprocess.healthcond.certificate.v3.HosPersonal source) {
    CertificateStatusUpdateForCareDTO.Intyg.SkapadAv target = new CertificateStatusUpdateForCareDTO.Intyg.SkapadAv();
    target.setPersonalId(convertPersonalId(source.getPersonalId()));
    target.setFullstandigtNamn(source.getFullstandigtNamn());
    target.setForskrivarkod(source.getForskrivarkod());
    target.setBefattning(convertBefattning(source.getBefattning().get(0)));
    target.setEnhet(convertEnhet(source.getEnhet()));
    return target;
  }

  private CertificateStatusUpdateForCareDTO.Intyg.SkapadAv.PersonalId convertPersonalId(
      se.riv.clinicalprocess.healthcond.certificate.types.v3.HsaId source) {
    CertificateStatusUpdateForCareDTO.Intyg.SkapadAv.PersonalId target = new CertificateStatusUpdateForCareDTO.Intyg.SkapadAv.PersonalId();
    target.setRoot(source.getRoot());
    target.setExtension(source.getExtension());
    return target;
  }

  private CertificateStatusUpdateForCareDTO.Intyg.SkapadAv.Befattning convertBefattning(
      se.riv.clinicalprocess.healthcond.certificate.types.v3.Befattning source) {
    CertificateStatusUpdateForCareDTO.Intyg.SkapadAv.Befattning target = new CertificateStatusUpdateForCareDTO.Intyg.SkapadAv.Befattning();
    target.setCode(source.getCode());
    target.setCodeSystem(source.getCodeSystem());
    target.setDisplayName(source.getDisplayName());
    return target;
  }

  private CertificateStatusUpdateForCareDTO.Intyg.SkapadAv.Enhet convertEnhet(
      se.riv.clinicalprocess.healthcond.certificate.v3.Enhet source) {
    CertificateStatusUpdateForCareDTO.Intyg.SkapadAv.Enhet target = new CertificateStatusUpdateForCareDTO.Intyg.SkapadAv.Enhet();
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

  private CertificateStatusUpdateForCareDTO.Intyg.SkapadAv.Enhet.EnhetsId convertEnhetsId(
      se.riv.clinicalprocess.healthcond.certificate.types.v3.HsaId source) {
    CertificateStatusUpdateForCareDTO.Intyg.SkapadAv.Enhet.EnhetsId target = new CertificateStatusUpdateForCareDTO.Intyg.SkapadAv.Enhet.EnhetsId();
    target.setRoot(source.getRoot());
    target.setExtension(source.getExtension());
    return target;
  }

  private CertificateStatusUpdateForCareDTO.Intyg.SkapadAv.Enhet.Arbetsplatskod convertArbetsplatskod(
      se.riv.clinicalprocess.healthcond.certificate.types.v3.ArbetsplatsKod source) {
    CertificateStatusUpdateForCareDTO.Intyg.SkapadAv.Enhet.Arbetsplatskod target = new CertificateStatusUpdateForCareDTO.Intyg.SkapadAv.Enhet.Arbetsplatskod();
    target.setRoot(source.getRoot());
    target.setExtension(source.getExtension());
    return target;
  }

  private CertificateStatusUpdateForCareDTO.Intyg.SkapadAv.Enhet.Vardgivare convertVardgivare(
      se.riv.clinicalprocess.healthcond.certificate.v3.Vardgivare source) {
    CertificateStatusUpdateForCareDTO.Intyg.SkapadAv.Enhet.Vardgivare target = new CertificateStatusUpdateForCareDTO.Intyg.SkapadAv.Enhet.Vardgivare();
    target.setVardgivareId(convertVardgivareId(source.getVardgivareId()));
    target.setVardgivarnamn(source.getVardgivarnamn());
    return target;
  }

  private CertificateStatusUpdateForCareDTO.Intyg.SkapadAv.Enhet.Vardgivare.VardgivareId convertVardgivareId(
      se.riv.clinicalprocess.healthcond.certificate.types.v3.HsaId source) {
    CertificateStatusUpdateForCareDTO.Intyg.SkapadAv.Enhet.Vardgivare.VardgivareId target = new CertificateStatusUpdateForCareDTO.Intyg.SkapadAv.Enhet.Vardgivare.VardgivareId();
    target.setRoot(source.getRoot());
    target.setExtension(source.getExtension());
    return target;
  }

  private CertificateStatusUpdateForCareDTO.Handelse.Handelsekod convertHandelsekod(
      se.riv.clinicalprocess.healthcond.certificate.types.v3.Handelsekod source) {
    CertificateStatusUpdateForCareDTO.Handelse.Handelsekod target = new CertificateStatusUpdateForCareDTO.Handelse.Handelsekod();
    target.setCode(source.getCode());
    target.setCodeSystem(source.getCodeSystem());
    target.setDisplayName(source.getDisplayName());
    return target;
  }

  private CertificateStatusUpdateForCareDTO.Fragor convertFragor(
      se.riv.clinicalprocess.healthcond.certificate.v3.Arenden source) {
    CertificateStatusUpdateForCareDTO.Fragor target = new CertificateStatusUpdateForCareDTO.Fragor();
    target.setTotalt(source.getTotalt());
    target.setEjBesvarade(source.getEjBesvarade());
    target.setBesvarade(source.getBesvarade());
    target.setHanterade(source.getHanterade());
    return target;
  }
}