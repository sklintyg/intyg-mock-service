package se.inera.intyg.intygmockservice.domain.navigation.model;

import java.util.Objects;

public record PersonId(String value) {

  public PersonId {
    Objects.requireNonNull(value, "PersonId value must not be null");
  }

  public static PersonId of(final String raw) {
    return new PersonId(raw);
  }

  public String normalized() {
    return value.replace("-", "");
  }

  public boolean matchesIgnoringHyphens(final PersonId other) {
    if (other == null) {
      return false;
    }
    return normalized().equals(other.normalized());
  }
}
