package se.inera.intyg.intygmockservice.application.navigation.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import se.inera.intyg.intygmockservice.application.navigation.certificate.CertificateAssembler;
import se.inera.intyg.intygmockservice.application.navigation.certificate.CertificateResponse;
import se.inera.intyg.intygmockservice.domain.navigation.model.Certificate;
import se.inera.intyg.intygmockservice.domain.navigation.model.Patient;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

  @Mock private PatientNavigationService service;
  @Mock private PatientAssembler assembler;
  @Mock private CertificateAssembler certificateAssembler;

  @InjectMocks private PatientController controller;

  @Test
  void getPatientByPersonId_ShouldReturn200WhenFound() {
    final var patient = Patient.builder().personId("191212121212").build();
    final var entityModel =
        EntityModel.of(new PatientResponse("191212121212", null, null, null, null, null));

    when(service.findByPersonId("191212121212")).thenReturn(Optional.of(patient));
    when(assembler.toModel(patient)).thenReturn(entityModel);

    final var response = controller.getPatientByPersonId("191212121212");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void getPatientByPersonId_ShouldReturn404WhenNotFound() {
    when(service.findByPersonId("unknown")).thenReturn(Optional.empty());

    final var response = controller.getPatientByPersonId("unknown");

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void getPatientCertificates_ShouldDelegateToServiceAndAssembler() {
    final var certificate = Certificate.builder().certificateId("cert-001").build();
    final var collectionModel = CollectionModel.<EntityModel<CertificateResponse>>empty();

    when(service.findCertificatesByPersonId("191212121212")).thenReturn(List.of(certificate));
    when(certificateAssembler.toCollectionModel(List.of(certificate))).thenReturn(collectionModel);

    final var result = controller.getPatientCertificates("191212121212");

    assertNotNull(result);
    verify(service).findCertificatesByPersonId("191212121212");
    verify(certificateAssembler).toCollectionModel(List.of(certificate));
  }

  @Test
  void getPatientMessages_ShouldReturnEmptyCollection() {
    final var response = controller.getPatientMessages("191212121212");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }
}
