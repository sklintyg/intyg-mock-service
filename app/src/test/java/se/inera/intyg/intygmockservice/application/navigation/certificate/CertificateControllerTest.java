package se.inera.intyg.intygmockservice.application.navigation.certificate;

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
import se.inera.intyg.intygmockservice.domain.navigation.model.Certificate;

@ExtendWith(MockitoExtension.class)
class CertificateControllerTest {

  @Mock private CertificateNavigationService service;
  @Mock private CertificateAssembler assembler;

  @InjectMocks private CertificateController controller;

  @Test
  void getAllCertificates_ShouldDelegateToServiceAndAssembler() {
    final var certificate = Certificate.builder().certificateId("cert-001").build();
    final var collectionModel = CollectionModel.<EntityModel<CertificateResponse>>empty();

    when(service.findAll()).thenReturn(List.of(certificate));
    when(assembler.toCollectionModel(List.of(certificate))).thenReturn(collectionModel);

    final var result = controller.getAllCertificates();

    assertNotNull(result);
    verify(service).findAll();
    verify(assembler).toCollectionModel(List.of(certificate));
  }

  @Test
  void getCertificateById_ShouldReturn200WithModelWhenFound() {
    final var certificate = Certificate.builder().certificateId("cert-001").build();
    final var entityModel =
        EntityModel.of(
            new CertificateResponse(null, null, null, null, null, null, null, null, null));

    when(service.findById("cert-001")).thenReturn(Optional.of(certificate));
    when(assembler.toModel(certificate)).thenReturn(entityModel);

    final var response = controller.getCertificateById("cert-001");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void getCertificateById_ShouldReturn404WhenNotFound() {
    when(service.findById("cert-unknown")).thenReturn(Optional.empty());

    final var response = controller.getCertificateById("cert-unknown");

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void getCertificateMessages_ShouldReturnEmptyCollection() {
    final var response = controller.getCertificateMessages("cert-001");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void getCertificateStatusUpdates_ShouldReturnEmptyCollection() {
    final var response = controller.getCertificateStatusUpdates("cert-001");

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void getCertificateLogEntries_ShouldReturnEmptyCollection() {
    final var response = controller.getCertificateLogEntries("cert-001");

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void getCertificateRevocation_ShouldReturn404() {
    final var response = controller.getCertificateRevocation("cert-001");

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }
}
