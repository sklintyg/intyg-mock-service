package se.inera.intyg.intygmockservice.revokecertificate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.intygmockservice.common.dto.IntygDTO.IntygsId;
import se.inera.intyg.intygmockservice.revokecertificate.converter.RevokeCertificateConverter;
import se.inera.intyg.intygmockservice.revokecertificate.dto.RevokeCertificateDTO;
import se.inera.intyg.intygmockservice.revokecertificate.repository.RevokeCertificateRepository;
import se.riv.clinicalprocess.healthcond.certificate.revokeCertificate.v2.RevokeCertificateType;

@ExtendWith(MockitoExtension.class)
class RevokeCertificateServiceTest {

  private static final String LOGICAL_ADDRESS = "logical-address-1";
  private static final String CERTIFICATE_ID = "cert-123";

  @Mock private RevokeCertificateRepository repository;
  @Mock private RevokeCertificateConverter converter;

  @InjectMocks private RevokeCertificateService service;

  @Test
  void shouldAddToRepositoryWhenStore() {
    final var type = new RevokeCertificateType();
    final var dto =
        RevokeCertificateDTO.builder()
            .intygsId(IntygsId.builder().root("root").extension(CERTIFICATE_ID).build())
            .meddelande("reason")
            .build();
    when(converter.convert(type)).thenReturn(dto);

    service.store(LOGICAL_ADDRESS, type);

    verify(repository).add(LOGICAL_ADDRESS, type);
  }

  @Test
  void shouldConvertWhenStore() {
    final var type = new RevokeCertificateType();
    final var dto =
        RevokeCertificateDTO.builder()
            .intygsId(IntygsId.builder().root("root").extension(CERTIFICATE_ID).build())
            .meddelande("reason")
            .build();
    when(converter.convert(type)).thenReturn(dto);

    service.store(LOGICAL_ADDRESS, type);

    verify(converter).convert(type);
  }

  @Test
  void shouldReturnAllWhenGetAll() {
    final var type = new RevokeCertificateType();
    final var dto = RevokeCertificateDTO.builder().build();
    when(repository.findAll()).thenReturn(List.of(type));
    when(converter.convert(type)).thenReturn(dto);

    final var result = service.getAll();

    assertEquals(List.of(dto), result);
  }

  @Test
  void shouldReturnDtoWhenGetByIdExists() {
    final var type = new RevokeCertificateType();
    final var dto = RevokeCertificateDTO.builder().build();
    when(repository.findByCertificateId(CERTIFICATE_ID)).thenReturn(Optional.of(type));
    when(converter.convert(type)).thenReturn(dto);

    final var result = service.getById(CERTIFICATE_ID);

    assertTrue(result.isPresent());
    assertEquals(dto, result.get());
  }

  @Test
  void shouldReturnEmptyWhenGetByIdNotFound() {
    when(repository.findByCertificateId(CERTIFICATE_ID)).thenReturn(Optional.empty());

    final var result = service.getById(CERTIFICATE_ID);

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnDtosWhenGetByLogicalAddress() {
    final var type = new RevokeCertificateType();
    final var dto = RevokeCertificateDTO.builder().build();
    when(repository.findByLogicalAddress(LOGICAL_ADDRESS)).thenReturn(List.of(type));
    when(converter.convert(type)).thenReturn(dto);

    final var result = service.getByLogicalAddress(LOGICAL_ADDRESS);

    assertEquals(List.of(dto), result);
  }

  @Test
  void shouldNormalizePersonIdWhenGetByPersonId() {
    when(repository.findByPersonId("191212121212")).thenReturn(List.of());

    service.getByPersonId("19121212-1212");

    verify(repository).findByPersonId("191212121212");
  }

  @Test
  void shouldReturnDtosWhenGetByPersonId() {
    final var type = new RevokeCertificateType();
    final var dto = RevokeCertificateDTO.builder().build();
    when(repository.findByPersonId(any())).thenReturn(List.of(type));
    when(converter.convert(type)).thenReturn(dto);

    final var result = service.getByPersonId("191212121212");

    assertEquals(List.of(dto), result);
  }

  @Test
  void shouldDelegateToRepositoryWhenDeleteAll() {
    service.deleteAll();

    verify(repository).deleteAll();
  }

  @Test
  void shouldDelegateToRepositoryWhenDeleteById() {
    service.deleteById(CERTIFICATE_ID);

    verify(repository).deleteById(CERTIFICATE_ID);
  }
}
