package se.inera.intyg.intygmockservice.domain.navigation.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PersonIdNormalizationTest {

  @Test
  void normalizationStripsHyphens() {
    assertEquals("191212121212", PersonId.of("19121212-1212").normalized());
  }

  @Test
  void normalizationIsIdempotent() {
    assertEquals("191212121212", PersonId.of("191212121212").normalized());
  }

  @Test
  void creatingPersonIdWithNullValueThrows() {
    assertThrows(NullPointerException.class, () -> PersonId.of(null));
  }

  @Test
  void personIdsWithAndWithoutHyphensMatch() {
    assertTrue(PersonId.of("19121212-1212").matchesIgnoringHyphens(PersonId.of("191212121212")));
  }

  @Test
  void differentPersonIdsDoNotMatch() {
    assertFalse(PersonId.of("19121212-1212").matchesIgnoringHyphens(PersonId.of("200001011234")));
  }

  @Test
  void matchingAgainstNullArgumentReturnsFalse() {
    assertFalse(PersonId.of("191212121212").matchesIgnoringHyphens(null));
  }
}
