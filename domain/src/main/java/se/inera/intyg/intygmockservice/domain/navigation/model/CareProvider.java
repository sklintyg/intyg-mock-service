package se.inera.intyg.intygmockservice.domain.navigation.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CareProvider {

  String careProviderId;
  String careProviderName;
}
