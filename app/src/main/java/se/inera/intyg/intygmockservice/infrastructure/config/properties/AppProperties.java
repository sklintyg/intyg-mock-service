package se.inera.intyg.intygmockservice.infrastructure.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public record AppProperties(@NotNull @Valid Repository repository) {

  public record Repository(
      @NotNull @Valid RepositoryConfig registerCertificate,
      @NotNull @Valid RepositoryConfig revokeCertificate,
      @NotNull @Valid RepositoryConfig sendMessageToRecipient,
      @NotNull @Valid RepositoryConfig certificateStatusUpdateForCare,
      @NotNull @Valid RepositoryConfig storeLog) {}

  public record RepositoryConfig(@Min(1) int maxSize) {}
}
