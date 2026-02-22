package se.inera.intyg.intygmockservice.storelog.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import se.inera.intyg.intygmockservice.common.repository.AbstractInMemoryRepository;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogType;

@Repository
public class StoreLogTypeRepository extends AbstractInMemoryRepository<StoreLogType> {

    public List<StoreLogType> findByUserId(String userId) {
        return null;
    }
}