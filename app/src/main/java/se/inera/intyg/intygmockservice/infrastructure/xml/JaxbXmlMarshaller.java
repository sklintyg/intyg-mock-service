package se.inera.intyg.intygmockservice.infrastructure.xml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import javax.xml.namespace.QName;
import org.springframework.stereotype.Component;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareType;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientType;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogType;
import se.riv.informationsecurity.auditing.log.v2.LogType;

@Component
public class JaxbXmlMarshaller {

  private static final JAXBContext JAXB_CONTEXT;

  static {
    try {
      JAXB_CONTEXT =
          JAXBContext.newInstance(
              "se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3"
                  + ":se.riv.clinicalprocess.healthcond.certificate.v3"
                  + ":se.riv.clinicalprocess.healthcond.certificate.types.v3"
                  + ":org.w3._2000._09.xmldsig_"
                  + ":org.w3._2002._06.xmldsig_filter2"
                  + ":se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2"
                  + ":se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2"
                  + ":se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3"
                  + ":se.riv.informationsecurity.auditing.log.StoreLogResponder.v2"
                  + ":se.riv.informationsecurity.auditing.log.v2");
    } catch (JAXBException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  public String marshal(Object jaxbType) {
    final var element = wrapWithElement(jaxbType);
    try {
      final var marshaller = JAXB_CONTEXT.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      final var sw = new StringWriter();
      marshaller.marshal(element, sw);
      return sw.toString();
    } catch (JAXBException e) {
      throw new IllegalStateException("Failed to marshal to XML", e);
    }
  }

  private JAXBElement<?> wrapWithElement(Object jaxbType) {
    return switch (jaxbType) {
      case RegisterCertificateType t ->
          new se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.ObjectFactory()
              .createRegisterCertificate(t);
      case RevokeCertificateType t ->
          new se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.ObjectFactory()
              .createRevokeCertificate(t);
      case SendMessageToRecipientType t ->
          new se.riv
                  .clinicalprocess
                  .healthcond
                  .certificate
                  .sendMessageToRecipient
                  .v2
                  .ObjectFactory()
              .createSendMessageToRecipient(t);
      case CertificateStatusUpdateForCareType t ->
          new se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder
                  .v3.ObjectFactory()
              .createCertificateStatusUpdateForCare(t);
      case StoreLogType t ->
          new se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.ObjectFactory()
              .createStoreLog(t);
      case LogType t ->
          new JAXBElement<>(
              new QName("urn:riv:informationsecurity:auditing:log:2", "log"), LogType.class, t);
      default ->
          throw new IllegalArgumentException(
              "Unsupported JAXB type: " + jaxbType.getClass().getName());
    };
  }
}
