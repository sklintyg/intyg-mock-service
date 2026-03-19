package se.inera.intyg.intygmockservice.infrastructure.repository;

import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.infrastructure.config.properties.AppProperties;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogType;

@Repository
public class StoreLogTypeRepository extends AbstractInMemoryRepository<StoreLogType> {

  public StoreLogTypeRepository(AppProperties appProperties) {
    super(appProperties.repository().storeLog().maxSize());
  }
}
