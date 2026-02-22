package se.inera.intyg.intygmockservice.storelog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se.inera.intyg.intygmockservice.storelog.converter.StoreLogTypeConverter;
import se.inera.intyg.intygmockservice.storelog.repository.StoreLogTypeRepository;
import se.riv.informationsecurity.auditing.log.StoreLog.v2.rivtabp21.StoreLogResponderInterface;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogResponseType;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogType;
import se.riv.informationsecurity.auditing.log.v2.ResultType;

@RequiredArgsConstructor
@Slf4j
public class StoreLogResponderImpl implements StoreLogResponderInterface {

    private final StoreLogTypeConverter storeLogTypeConverter;
    private final StoreLogTypeRepository storeLogTypeRepository;

    @Override
    public StoreLogResponseType storeLog(String logicalAddress, StoreLogType storeLogType) {
        storeLogTypeRepository.add(logicalAddress, storeLogType);

        log.atInfo().setMessage(
                "Stored log received for logical address '%s' with '%s' logs".formatted(
                    logicalAddress,
                    storeLogType.getLog().size()
                )
            )
            .addKeyValue("event.logical_address", logicalAddress)
            .log();

        final var response = new StoreLogResponseType();
        final ResultType resultType = new ResultType();
        resultType.setResultCode(se.riv.informationsecurity.auditing.log.v2.ResultCodeType.OK);
        response.setResult(resultType);
        return response;
    }
}
