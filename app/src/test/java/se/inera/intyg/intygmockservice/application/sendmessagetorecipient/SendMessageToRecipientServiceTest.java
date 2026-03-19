package se.inera.intyg.intygmockservice.sendmessagetorecipient;

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
import se.inera.intyg.intygmockservice.sendmessagetorecipient.converter.SendMessageToRecipientConverter;
import se.inera.intyg.intygmockservice.sendmessagetorecipient.dto.SendMessageToRecipientDTO;
import se.inera.intyg.intygmockservice.sendmessagetorecipient.passthrough.SendMessageToRecipientPassthroughClient;
import se.inera.intyg.intygmockservice.sendmessagetorecipient.repository.SendMessageToRecipientRepository;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientResponseType;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultCodeType;
import se.riv.clinicalprocess.healthcond.certificate.v3.ResultType;

@ExtendWith(MockitoExtension.class)
class SendMessageToRecipientServiceTest {

  private static final String LOGICAL_ADDRESS = "logical-address-1";
  private static final String MESSAGE_ID = "msg-123";
  private static final String CERTIFICATE_ID = "cert-123";

  @Mock private SendMessageToRecipientRepository repository;
  @Mock private SendMessageToRecipientConverter converter;
  @Mock private SendMessageToRecipientPassthroughClient passthroughClient;

  @InjectMocks private SendMessageToRecipientService service;

  @Test
  void shouldAddToRepositoryWhenStore() {
    final var type = new SendMessageToRecipientType();
    when(converter.convert(type)).thenReturn(buildDto());
    when(passthroughClient.forward(any(), any())).thenReturn(Optional.empty());

    service.store(LOGICAL_ADDRESS, type);

    verify(repository).add(LOGICAL_ADDRESS, type);
  }

  @Test
  void shouldDelegateToPassthroughClientWhenStore() {
    final var type = new SendMessageToRecipientType();
    when(converter.convert(type)).thenReturn(buildDto());
    when(passthroughClient.forward(any(), any())).thenReturn(Optional.empty());

    service.store(LOGICAL_ADDRESS, type);

    verify(passthroughClient).forward(LOGICAL_ADDRESS, type);
  }

  @Test
  void shouldReturnPassthroughResultWhenStore() {
    final var type = new SendMessageToRecipientType();
    when(converter.convert(type)).thenReturn(buildDto());
    final var response = okResponse();
    when(passthroughClient.forward(any(), any())).thenReturn(Optional.of(response));

    final var result = service.store(LOGICAL_ADDRESS, type);

    assertTrue(result.isPresent());
    assertEquals(response, result.get());
  }

  @Test
  void shouldReturnEmptyOptionalWhenPassthroughDisabled() {
    final var type = new SendMessageToRecipientType();
    when(converter.convert(type)).thenReturn(buildDto());
    when(passthroughClient.forward(any(), any())).thenReturn(Optional.empty());

    final var result = service.store(LOGICAL_ADDRESS, type);

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnAllWhenGetAll() {
    final var type = new SendMessageToRecipientType();
    final var dto = SendMessageToRecipientDTO.builder().build();
    when(repository.findAll()).thenReturn(List.of(type));
    when(converter.convert(type)).thenReturn(dto);

    final var result = service.getAll();

    assertEquals(List.of(dto), result);
  }

  @Test
  void shouldReturnDtoWhenGetByMessageIdExists() {
    final var type = new SendMessageToRecipientType();
    final var dto = SendMessageToRecipientDTO.builder().build();
    when(repository.findByMessageId(MESSAGE_ID)).thenReturn(Optional.of(type));
    when(converter.convert(type)).thenReturn(dto);

    final var result = service.getByMessageId(MESSAGE_ID);

    assertTrue(result.isPresent());
    assertEquals(dto, result.get());
  }

  @Test
  void shouldReturnEmptyWhenGetByMessageIdNotFound() {
    when(repository.findByMessageId(MESSAGE_ID)).thenReturn(Optional.empty());

    final var result = service.getByMessageId(MESSAGE_ID);

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnDtosWhenGetByCertificateId() {
    final var type = new SendMessageToRecipientType();
    final var dto = SendMessageToRecipientDTO.builder().build();
    when(repository.findByCertificateId(CERTIFICATE_ID)).thenReturn(List.of(type));
    when(converter.convert(type)).thenReturn(dto);

    final var result = service.getByCertificateId(CERTIFICATE_ID);

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
    final var type = new SendMessageToRecipientType();
    final var dto = SendMessageToRecipientDTO.builder().build();
    when(repository.findByPersonId(any())).thenReturn(List.of(type));
    when(converter.convert(type)).thenReturn(dto);

    final var result = service.getByPersonId("191212121212");

    assertEquals(List.of(dto), result);
  }

  @Test
  void shouldReturnDtosWhenGetByLogicalAddress() {
    final var type = new SendMessageToRecipientType();
    final var dto = SendMessageToRecipientDTO.builder().build();
    when(repository.findByLogicalAddress(LOGICAL_ADDRESS)).thenReturn(List.of(type));
    when(converter.convert(type)).thenReturn(dto);

    final var result = service.getByLogicalAddress(LOGICAL_ADDRESS);

    assertEquals(List.of(dto), result);
  }

  @Test
  void shouldDelegateToRepositoryWhenDeleteAll() {
    service.deleteAll();

    verify(repository).deleteAll();
  }

  @Test
  void shouldDelegateToRepositoryWhenDeleteByMessageId() {
    service.deleteByMessageId(MESSAGE_ID);

    verify(repository).deleteByMessageId(MESSAGE_ID);
  }

  private SendMessageToRecipientDTO buildDto() {
    return SendMessageToRecipientDTO.builder()
        .meddelandeId(MESSAGE_ID)
        .intygsId(IntygsId.builder().root("root").extension(CERTIFICATE_ID).build())
        .meddelande("content")
        .build();
  }

  private SendMessageToRecipientResponseType okResponse() {
    final var response = new SendMessageToRecipientResponseType();
    final var result = new ResultType();
    result.setResultCode(ResultCodeType.OK);
    response.setResult(result);
    return response;
  }
}
