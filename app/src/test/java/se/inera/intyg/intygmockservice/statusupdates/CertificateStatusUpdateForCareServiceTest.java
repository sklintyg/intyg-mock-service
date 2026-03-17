package se.inera.intyg.intygmockservice.statusupdates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.intygmockservice.common.dto.IntygDTO;
import se.inera.intyg.intygmockservice.common.dto.IntygDTO.IntygsId;
import se.inera.intyg.intygmockservice.statusupdates.converter.CertificateStatusUpdateForCareConverter;
import se.inera.intyg.intygmockservice.statusupdates.dto.CertificateStatusUpdateForCareDTO;
import se.inera.intyg.intygmockservice.statusupdates.dto.CertificateStatusUpdateForCareDTO.Handelse;
import se.inera.intyg.intygmockservice.statusupdates.dto.CertificateStatusUpdateForCareDTO.Handelse.Handelsekod;
import se.inera.intyg.intygmockservice.statusupdates.repository.CertificateStatusUpdateForCareRepository;
import se.riv.clinicalprocess.healthcond.certificate.certificatestatusupdateforcareresponder.v3.CertificateStatusUpdateForCareType;

@ExtendWith(MockitoExtension.class)
class CertificateStatusUpdateForCareServiceTest {

  private static final String LOGICAL_ADDRESS = "logical-address-1";
  private static final String CERTIFICATE_ID = "cert-123";
  private static final String PERSON_ID = "191212121212";
  private static final String EVENT_CODE = "SKAPAT";

  @Mock private CertificateStatusUpdateForCareRepository repository;
  @Mock private CertificateStatusUpdateForCareConverter converter;

  @InjectMocks private CertificateStatusUpdateForCareService service;

  private static CertificateStatusUpdateForCareDTO buildDto(String certificateId) {
    return CertificateStatusUpdateForCareDTO.builder()
        .intyg(
            IntygDTO.builder()
                .intygsId(IntygsId.builder().root("root").extension(certificateId).build())
                .build())
        .handelse(
            Handelse.builder().handelsekod(Handelsekod.builder().code(EVENT_CODE).build()).build())
        .build();
  }

  @Test
  void shouldAddToRepositoryWhenStore() {
    final var type = new CertificateStatusUpdateForCareType();
    when(converter.convert(type)).thenReturn(buildDto(CERTIFICATE_ID));

    service.store(LOGICAL_ADDRESS, type);

    verify(repository).add(LOGICAL_ADDRESS, type);
  }

  @Test
  void shouldConvertWhenStore() {
    final var type = new CertificateStatusUpdateForCareType();
    when(converter.convert(type)).thenReturn(buildDto(CERTIFICATE_ID));

    service.store(LOGICAL_ADDRESS, type);

    verify(converter).convert(type);
  }

  @Test
  void shouldReturnAllWhenGetAll() {
    final var type = new CertificateStatusUpdateForCareType();
    final var dto = CertificateStatusUpdateForCareDTO.builder().build();
    when(repository.findAll()).thenReturn(List.of(type));
    when(converter.convert(type)).thenReturn(dto);

    final var result = service.getAll();

    assertEquals(List.of(dto), result);
  }

  @Test
  void shouldReturnDtosWhenGetByCertificateId() {
    final var type = new CertificateStatusUpdateForCareType();
    final var dto = CertificateStatusUpdateForCareDTO.builder().build();
    when(repository.findByCertificateId(CERTIFICATE_ID)).thenReturn(List.of(type));
    when(converter.convert(type)).thenReturn(dto);

    final var result = service.getByCertificateId(CERTIFICATE_ID);

    assertEquals(List.of(dto), result);
  }

  @Test
  void shouldReturnDtosWhenGetByLogicalAddress() {
    final var type = new CertificateStatusUpdateForCareType();
    final var dto = CertificateStatusUpdateForCareDTO.builder().build();
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
    final var type = new CertificateStatusUpdateForCareType();
    final var dto = CertificateStatusUpdateForCareDTO.builder().build();
    when(repository.findByPersonId(any())).thenReturn(List.of(type));
    when(converter.convert(type)).thenReturn(dto);

    final var result = service.getByPersonId(PERSON_ID);

    assertEquals(List.of(dto), result);
  }

  @Test
  void shouldReturnDtosWhenGetByEventCode() {
    final var type = new CertificateStatusUpdateForCareType();
    final var dto = CertificateStatusUpdateForCareDTO.builder().build();
    when(repository.findByEventCode(EVENT_CODE)).thenReturn(List.of(type));
    when(converter.convert(type)).thenReturn(dto);

    final var result = service.getByEventCode(EVENT_CODE);

    assertEquals(List.of(dto), result);
  }

  @Test
  void shouldDelegateToRepositoryWhenDeleteAll() {
    service.deleteAll();

    verify(repository).deleteAll();
  }

  @Test
  void shouldDelegateToRepositoryWhenDeleteByCertificateId() {
    service.deleteByCertificateId(CERTIFICATE_ID);

    verify(repository).deleteByCertificateId(CERTIFICATE_ID);
  }
}
