package se.inera.intyg.intygmockservice.domain.navigation.model;

import java.util.List;

public record PageResult<T>(List<T> content, int page, int size, long totalElements) {

  public PageResult {
    content = List.copyOf(content);
  }

  @Override
  public List<T> content() {
    return List.copyOf(content);
  }

  public long totalPages() {
    return size == 0 ? 0 : (totalElements + size - 1) / size;
  }

  public boolean hasNext() {
    return page + 1 < totalPages();
  }

  public boolean hasPrevious() {
    return page > 0;
  }
}
