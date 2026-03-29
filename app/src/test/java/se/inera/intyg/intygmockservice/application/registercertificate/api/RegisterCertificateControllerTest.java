package se.inera.intyg.intygmockservice.application.registercertificate.api;

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
import org.springframework.http.HttpStatus;
import se.inera.intyg.intygmockservice.application.registercertificate.dto.RegisterCertificateDTO;
import se.inera.intyg.intygmockservice.application.registercertificate.service.RegisterCertificateService;

@ExtendWith(MockitoExtension.class)
class RegisterCertificateControllerTest {

  private static final String CERTIFICATE_ID = "cert-123";
  private static final String LOGICAL_ADDRESS = "logical-address-1";
  private static final String PERSON_ID = "191212121212";

  @Mock private RegisterCertificateService registerCertificateService;

  @InjectMocks private RegisterCertificateController controller;

  @Test
  void getAllRegisteredCertificates_ShouldReturnAllFromService() {
    final var dto = RegisterCertificateDTO.builder().build();
    when(registerCertificateService.getAll()).thenReturn(List.of(dto));

    final var result = controller.getAllRegisteredCertificates();

    assertEquals(List.of(dto), result);
  }

  @Test
  void getCertificateAsXml_ShouldReturn200WithXmlWhenFound() {
    when(registerCertificateService.getAsXml(CERTIFICATE_ID)).thenReturn(Optional.of("<xml/>"));

    final var response = controller.getCertificateAsXml(CERTIFICATE_ID);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("<xml/>", response.getBody());
  }

  @Test
  void getCertificateAsXml_ShouldReturn404WhenNotFound() {
    when(registerCertificateService.getAsXml(CERTIFICATE_ID)).thenReturn(Optional.empty());

    final var response = controller.getCertificateAsXml(CERTIFICATE_ID);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void getCertificateById_ShouldReturn200WithDtoWhenFound() {
    final var dto = RegisterCertificateDTO.builder().build();
    when(registerCertificateService.getById(CERTIFICATE_ID)).thenReturn(Optional.of(dto));

    final var response = controller.getCertificateById(CERTIFICATE_ID);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(dto, response.getBody());
  }

  @Test
  void getCertificateById_ShouldReturn404WhenNotFound() {
    when(registerCertificateService.getById(CERTIFICATE_ID)).thenReturn(Optional.empty());

    final var response = controller.getCertificateById(CERTIFICATE_ID);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void getCertificatesByLogicalAddress_ShouldDelegateToService() {
    final var dto = RegisterCertificateDTO.builder().build();
    when(registerCertificateService.getByLogicalAddress(LOGICAL_ADDRESS)).thenReturn(List.of(dto));

    final var result = controller.getCertificatesByLogicalAddress(LOGICAL_ADDRESS);

    assertEquals(List.of(dto), result);
  }

  @Test
  void getCertificatesByPersonId_ShouldDelegateToService() {
    final var dto = RegisterCertificateDTO.builder().build();
    when(registerCertificateService.getByPersonId(PERSON_ID)).thenReturn(List.of(dto));

    final var result = controller.getCertificatesByPersonId(PERSON_ID);

    assertEquals(List.of(dto), result);
  }

  @Test
  void getCount_ShouldReturnCountFromService() {
    when(registerCertificateService.getCount()).thenReturn(42);

    final var response = controller.getCount();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(42, response.getBody().count());
  }

  @Test
  void deleteAllRegisteredCertificates_ShouldDelegateToService() {
    controller.deleteAllRegisteredCertificates();

    verify(registerCertificateService).deleteAll();
  }

  @Test
  void deleteCertificateById_ShouldDelegateToService() {
    controller.deleteCertificateById(CERTIFICATE_ID);

    verify(registerCertificateService).deleteById(CERTIFICATE_ID);
  }
}
