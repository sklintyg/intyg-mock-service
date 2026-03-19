package se.inera.intyg.intygmockservice.application.storelog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.intygmockservice.application.storelog.converter.StoreLogTypeConverter;
import se.inera.intyg.intygmockservice.infrastructure.passthrough.StoreLogPassthroughClient;
import se.inera.intyg.intygmockservice.infrastructure.repository.StoreLogTypeRepository;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogResponseType;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogType;
import se.riv.informationsecurity.auditing.log.v2.ResultCodeType;
import se.riv.informationsecurity.auditing.log.v2.ResultType;

@ExtendWith(MockitoExtension.class)
class StoreLogServiceTest {

  private static final String LOGICAL_ADDRESS = "logical-address-1";

  @Mock private StoreLogTypeRepository repository;
  @Mock private StoreLogTypeConverter converter;
  @Mock private StoreLogPassthroughClient passthroughClient;

  @InjectMocks private StoreLogService service;

  @Test
  void shouldAddToRepositoryWhenStore() {
    final var type = new StoreLogType();
    when(passthroughClient.forward(any(), any())).thenReturn(Optional.empty());

    service.store(LOGICAL_ADDRESS, type);

    verify(repository).add(LOGICAL_ADDRESS, type);
  }

  @Test
  void shouldDelegateToPassthroughClientWhenStore() {
    final var type = new StoreLogType();
    when(passthroughClient.forward(any(), any())).thenReturn(Optional.empty());

    service.store(LOGICAL_ADDRESS, type);

    verify(passthroughClient).forward(LOGICAL_ADDRESS, type);
  }

  @Test
  void shouldReturnPassthroughResultWhenStore() {
    final var type = new StoreLogType();
    final var response = okResponse();
    when(passthroughClient.forward(any(), any())).thenReturn(Optional.of(response));

    final var result = service.store(LOGICAL_ADDRESS, type);

    assertTrue(result.isPresent());
    assertEquals(response, result.get());
  }

  @Test
  void shouldReturnEmptyOptionalWhenPassthroughDisabled() {
    final var type = new StoreLogType();
    when(passthroughClient.forward(any(), any())).thenReturn(Optional.empty());

    final var result = service.store(LOGICAL_ADDRESS, type);

    assertTrue(result.isEmpty());
  }

  private StoreLogResponseType okResponse() {
    final var response = new StoreLogResponseType();
    final var result = new ResultType();
    result.setResultCode(ResultCodeType.OK);
    response.setResult(result);
    return response;
  }
}
