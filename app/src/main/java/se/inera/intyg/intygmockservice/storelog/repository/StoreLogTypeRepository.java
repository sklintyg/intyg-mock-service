package se.inera.intyg.intygmockservice.storelog.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.common.repository.AbstractInMemoryRepository;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogType;

@Repository
public class StoreLogTypeRepository extends AbstractInMemoryRepository<StoreLogType> {

    public StoreLogTypeRepository(
            @Value("${app.repository.store-log.max-size:1000}") int maxSize) {
        super(maxSize);
    }
}
