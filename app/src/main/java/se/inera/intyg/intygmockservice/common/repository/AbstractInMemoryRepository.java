package se.inera.intyg.intygmockservice.common.repository;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public abstract class AbstractInMemoryRepository<T> {

  private final int maxSize;
  private final Map<String, List<T>> repository = new HashMap<>();
  private final Deque<Map.Entry<String, T>> insertionOrder = new ArrayDeque<>();

  protected AbstractInMemoryRepository(int maxSize) {
    this.maxSize = maxSize;
  }

  public void add(String logicalAddress, T item) {
    repository.computeIfAbsent(logicalAddress, k -> new ArrayList<>()).add(item);
    insertionOrder.addLast(Map.entry(logicalAddress, item));

    while (insertionOrder.size() > maxSize) {
      var oldest = insertionOrder.removeFirst();
      var list = repository.get(oldest.getKey());
      if (list != null) {
        final T toRemove = oldest.getValue();
        list.removeIf(i -> i == toRemove);
        if (list.isEmpty()) {
          repository.remove(oldest.getKey());
        }
      }
    }
  }

  public List<T> findAll() {
    return repository.values().stream().flatMap(List::stream).toList();
  }

  public List<T> findByKey(String key) {
    return repository.getOrDefault(key, List.of());
  }

  public void deleteAll() {
    repository.clear();
    insertionOrder.clear();
  }

  public void removeIf(Predicate<T> predicate) {
    insertionOrder.removeIf(entry -> predicate.test(entry.getValue()));
    repository.values().forEach(list -> list.removeIf(predicate));
    repository.entrySet().removeIf(entry -> entry.getValue().isEmpty());
  }
}
