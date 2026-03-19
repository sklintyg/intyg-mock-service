package se.inera.intyg.intygmockservice.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MatchCriteriaTest {

  private MatchContext context(String logicalAddress, String certificateId, String personId) {
    return MatchContext.builder()
        .logicalAddress(logicalAddress)
        .certificateId(certificateId)
        .personId(personId)
        .build();
  }

  @Test
  void allNullCriteriaMatchesAnyContext() {
    final var criteria = MatchCriteria.builder().build();

    assertTrue(criteria.matches(context("any-address", "any-cert", "any-person")));
  }

  @Test
  void logicalAddressMatchesExact() {
    final var criteria = MatchCriteria.builder().logicalAddress("addr-1").build();

    assertTrue(criteria.matches(context("addr-1", "any", "any")));
  }

  @Test
  void logicalAddressDoesNotMatchDifferent() {
    final var criteria = MatchCriteria.builder().logicalAddress("addr-1").build();

    assertFalse(criteria.matches(context("addr-2", "any", "any")));
  }

  @Test
  void certificateIdMatchesExact() {
    final var criteria = MatchCriteria.builder().certificateId("cert-123").build();

    assertTrue(criteria.matches(context("any", "cert-123", "any")));
  }

  @Test
  void certificateIdDoesNotMatchDifferent() {
    final var criteria = MatchCriteria.builder().certificateId("cert-123").build();

    assertFalse(criteria.matches(context("any", "cert-456", "any")));
  }

  @Test
  void personIdMatchesWithHyphenNormalization() {
    final var criteria = MatchCriteria.builder().personId("191212121212").build();

    assertTrue(criteria.matches(context("any", "any", "19121212-1212")));
  }

  @Test
  void personIdDoesNotMatchDifferent() {
    final var criteria = MatchCriteria.builder().personId("191212121212").build();

    assertFalse(criteria.matches(context("any", "any", "200001011234")));
  }

  @Test
  void personIdDoesNotMatchNullContextPersonId() {
    final var criteria = MatchCriteria.builder().personId("191212121212").build();

    assertFalse(criteria.matches(context("any", "any", null)));
  }

  @Test
  void multipleCriteriaRequireAllToMatch() {
    final var criteria =
        MatchCriteria.builder().logicalAddress("addr-1").certificateId("cert-123").build();

    assertTrue(criteria.matches(context("addr-1", "cert-123", "any")));
    assertFalse(criteria.matches(context("addr-2", "cert-123", "any")));
    assertFalse(criteria.matches(context("addr-1", "cert-456", "any")));
  }

  @Test
  void specificityCountsNonNullFields() {
    assertEquals(0, MatchCriteria.builder().build().specificity());
    assertEquals(1, MatchCriteria.builder().logicalAddress("a").build().specificity());
    assertEquals(
        2, MatchCriteria.builder().logicalAddress("a").certificateId("b").build().specificity());
    assertEquals(
        3,
        MatchCriteria.builder()
            .logicalAddress("a")
            .certificateId("b")
            .personId("c")
            .build()
            .specificity());
  }
}
