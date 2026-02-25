package se.inera.intyg.intygmockservice.common.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbstractInMemoryRepositoryTest {

    private static class TestRepository extends AbstractInMemoryRepository<String> {

        TestRepository(int maxSize) {
            super(maxSize);
        }
    }

    private TestRepository repository;

    @BeforeEach
    void setUp() {
        repository = new TestRepository(3);
    }

    @Test
    void add_BelowLimit_StoresAllItems() {
        repository.add("addr", "a");
        repository.add("addr", "b");
        repository.add("addr", "c");

        assertThat(repository.findAll()).containsExactlyInAnyOrder("a", "b", "c");
    }

    @Test
    void add_ExceedsLimit_EvictsOldestItem() {
        repository.add("addr", "a");
        repository.add("addr", "b");
        repository.add("addr", "c");
        repository.add("addr", "d");

        assertThat(repository.findAll()).hasSize(3);
        assertThat(repository.findAll()).doesNotContain("a");
        assertThat(repository.findAll()).contains("b", "c", "d");
    }

    @Test
    void add_ExceedsLimitAcrossAddresses_EvictsOldestGlobally() {
        repository.add("addr1", "a");
        repository.add("addr2", "b");
        repository.add("addr2", "c");
        repository.add("addr1", "d");

        assertThat(repository.findAll()).hasSize(3);
        assertThat(repository.findAll()).doesNotContain("a");
        assertThat(repository.findAll()).contains("b", "c", "d");
    }

    @Test
    void deleteAll_ClearsInsertionOrderSoLimitIsResetCorrectly() {
        repository.add("addr", "a");
        repository.add("addr", "b");
        repository.add("addr", "c");

        repository.deleteAll();

        repository.add("addr", "x");
        repository.add("addr", "y");
        repository.add("addr", "z");

        assertThat(repository.findAll()).containsExactlyInAnyOrder("x", "y", "z");
    }

    @Test
    void removeIf_KeepsSizeConsistentWithFifoQueue() {
        repository.add("addr", "a");
        repository.add("addr", "b");
        repository.add("addr", "c");

        repository.removeIf("b"::equals);

        repository.add("addr", "d");

        assertThat(repository.findAll()).containsExactlyInAnyOrder("a", "c", "d");
    }
}
