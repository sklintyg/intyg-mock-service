package se.inera.intyg.intygmockservice.common.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractInMemoryRepository<T> {

    private final Map<String, List<T>> repository = new HashMap<>();

    public void add(String logicalAddress, T item) {
        repository.computeIfAbsent(logicalAddress, k -> new ArrayList<>())
            .add(item);
    }

    public List<T> findAll() {
        return repository.values().stream()
            .flatMap(List::stream)
            .toList();
    }

    public void deleteAll() {
        repository.clear();
    }
}