package se.inera.intyg.intygmockservice.statusupdates;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import se.inera.intyg.intygmockservice.statusupdates.dto.CertificateStatusUpdateForCareDTO;

@ExtendWith(MockitoExtension.class)
class CertificateStatusForCareControllerTest {

  private static final String CERTIFICATE_ID = "cert-123";
  private static final String LOGICAL_ADDRESS = "logical-address-1";
  private static final String PERSON_ID = "191212121212";
  private static final String EVENT_CODE = "SKAPAT";

  @Mock private CertificateStatusUpdateForCareService service;

  @InjectMocks private CertificateStatusForCareController controller;

  @Test
  void getAllCertificateStatusUpdates_ShouldReturnAll() {
    final var dto = CertificateStatusUpdateForCareDTO.builder().build();
    when(service.getAll()).thenReturn(List.of(dto));

    final var result = controller.getAllCertificateStatusUpdates();

    assertThat(result).containsExactly(dto);
  }

  @Test
  void deleteAllCertificateStatusUpdates_ShouldDelegateToService() {
    controller.deleteAllCertificateStatusUpdates();

    verify(service).deleteAll();
  }

  @Test
  void getByCertificateId_ShouldReturnMatchingDtos() {
    final var dto = CertificateStatusUpdateForCareDTO.builder().build();
    when(service.getByCertificateId(CERTIFICATE_ID)).thenReturn(List.of(dto));

    final var result = controller.getByCertificateId(CERTIFICATE_ID);

    assertThat(result).containsExactly(dto);
  }

  @Test
  void deleteByCertificateId_ShouldReturn204() {
    final var response = controller.deleteByCertificateId(CERTIFICATE_ID);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(service).deleteByCertificateId(CERTIFICATE_ID);
  }

  @Test
  void getByLogicalAddress_ShouldReturnMatchingDtos() {
    final var dto = CertificateStatusUpdateForCareDTO.builder().build();
    when(service.getByLogicalAddress(LOGICAL_ADDRESS)).thenReturn(List.of(dto));

    final var result = controller.getByLogicalAddress(LOGICAL_ADDRESS);

    assertThat(result).containsExactly(dto);
  }

  @Test
  void getByPersonId_ShouldReturnMatchingDtos() {
    final var dto = CertificateStatusUpdateForCareDTO.builder().build();
    when(service.getByPersonId(PERSON_ID)).thenReturn(List.of(dto));

    final var result = controller.getByPersonId(PERSON_ID);

    assertThat(result).containsExactly(dto);
  }

  @Test
  void getByEventCode_ShouldReturnMatchingDtos() {
    final var dto = CertificateStatusUpdateForCareDTO.builder().build();
    when(service.getByEventCode(EVENT_CODE)).thenReturn(List.of(dto));

    final var result = controller.getByEventCode(EVENT_CODE);

    assertThat(result).containsExactly(dto);
  }
}
