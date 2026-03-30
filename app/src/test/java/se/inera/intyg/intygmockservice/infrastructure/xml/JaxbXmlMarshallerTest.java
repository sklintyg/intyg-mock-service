package se.inera.intyg.intygmockservice.infrastructure.xml;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareType;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientType;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogType;

class JaxbXmlMarshallerTest {

  private final JaxbXmlMarshaller marshaller = new JaxbXmlMarshaller();

  @Test
  void marshalsRegisterCertificateType() {
    final var xml = marshaller.marshal(new RegisterCertificateType());
    assertTrue(xml.contains("RegisterCertificate"));
  }

  @Test
  void marshalsRevokeCertificateType() {
    final var xml = marshaller.marshal(new RevokeCertificateType());
    assertTrue(xml.contains("RevokeCertificate"));
  }

  @Test
  void marshalsSendMessageToRecipientType() {
    final var xml = marshaller.marshal(new SendMessageToRecipientType());
    assertTrue(xml.contains("SendMessageToRecipient"));
  }

  @Test
  void marshalsCertificateStatusUpdateForCareType() {
    final var xml = marshaller.marshal(new CertificateStatusUpdateForCareType());
    assertTrue(xml.contains("CertificateStatusUpdateForCare"));
  }

  @Test
  void marshalsStoreLogType() {
    final var xml = marshaller.marshal(new StoreLogType());
    assertTrue(xml.contains("StoreLog"));
  }

  @Test
  void throwsForUnsupportedType() {
    assertThrows(IllegalArgumentException.class, () -> marshaller.marshal("unsupported"));
  }
}
