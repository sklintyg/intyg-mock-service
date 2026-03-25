package se.inera.intyg.intygmockservice.domain.behavior.model;

import lombok.Builder;
import lombok.Value;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;

@Value
@Builder
public class MatchCriteria {
  String logicalAddress;
  String certificateId;
  String personId;

  public boolean matches(MatchContext context) {
    if (logicalAddress != null && !logicalAddress.equals(context.getLogicalAddress())) {
      return false;
    }
    if (certificateId != null && !certificateId.equals(context.getCertificateId())) {
      return false;
    }
    if (personId != null && !matchesPersonId(context.getPersonId())) {
      return false;
    }
    return true;
  }

  public int specificity() {
    int count = 0;
    if (logicalAddress != null) {
      count++;
    }
    if (certificateId != null) {
      count++;
    }
    if (personId != null) {
      count++;
    }
    return count;
  }

  private boolean matchesPersonId(String contextPersonId) {
    if (contextPersonId == null) {
      return false;
    }
    return PersonId.of(personId).matchesIgnoringHyphens(PersonId.of(contextPersonId));
  }
}
