package se.inera.intyg.intygmockservice.domain.navigation.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class PaginationBehaviorTest {

  @Test
  void emptyResultHasZeroPages() {
    var result = new PageResult<>(List.of(), 0, 10, 0);

    assertEquals(0, result.totalPages());
  }

  @Test
  void elementsFewerThanPageSizeFitOnOnePage() {
    var result = new PageResult<>(List.of(), 0, 10, 2);

    assertEquals(1, result.totalPages());
  }

  @Test
  void elementsDividingEvenlyProduceExactPages() {
    var result = new PageResult<>(List.of(), 0, 3, 6);

    assertEquals(2, result.totalPages());
  }

  @Test
  void remainderElementsRequireAnAdditionalPage() {
    var result = new PageResult<>(List.of(), 0, 3, 7);

    assertEquals(3, result.totalPages());
  }

  @Test
  void zeroPageSizeProducesZeroPagesInsteadOfException() {
    var result = new PageResult<>(List.of(), 0, 0, 5);

    assertEquals(0, result.totalPages());
  }

  @Test
  void firstPageOfManyHasNextPage() {
    var result = new PageResult<>(List.of(), 0, 3, 9);

    assertTrue(result.hasNext());
  }

  @Test
  void lastPageHasNoNextPage() {
    var result = new PageResult<>(List.of(), 2, 3, 9);

    assertFalse(result.hasNext());
  }

  @Test
  void singlePageResultHasNoNextPage() {
    var result = new PageResult<>(List.of(), 0, 10, 5);

    assertFalse(result.hasNext());
  }

  @Test
  void emptyResultHasNoNextPage() {
    var result = new PageResult<>(List.of(), 0, 10, 0);

    assertFalse(result.hasNext());
  }

  @Test
  void firstPageHasNoPreviousPage() {
    var result = new PageResult<>(List.of(), 0, 10, 30);

    assertFalse(result.hasPrevious());
  }

  @Test
  void secondPageHasPreviousPage() {
    var result = new PageResult<>(List.of(), 1, 10, 30);

    assertTrue(result.hasPrevious());
  }
}
