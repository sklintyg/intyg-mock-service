package se.inera.intyg.intygmockservice.application.storelog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygmockservice.application.storelog.service.StoreLogService;
import se.riv.informationsecurity.auditing.log.StoreLog.v2.rivtabp21.StoreLogResponderInterface;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogResponseType;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogType;
import se.riv.informationsecurity.auditing.log.v2.ResultCodeType;
import se.riv.informationsecurity.auditing.log.v2.ResultType;

@Service
@RequiredArgsConstructor
public class StoreLogResponderImpl implements StoreLogResponderInterface {

  private final StoreLogService service;

  @Override
  public StoreLogResponseType storeLog(String logicalAddress, StoreLogType storeLogType) {
    return service
        .store(logicalAddress, storeLogType)
        .orElseGet(
            () -> {
              final var response = new StoreLogResponseType();
              final var result = new ResultType();
              result.setResultCode(ResultCodeType.OK);
              response.setResult(result);
              return response;
            });
  }
}
