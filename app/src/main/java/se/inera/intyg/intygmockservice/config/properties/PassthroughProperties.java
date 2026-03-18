package se.inera.intyg.intygmockservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.passthrough")
public record PassthroughProperties(MtlsConfig mtls, ServiceConfig registerCertificate) {

  public record ServiceConfig(boolean enabled, String url) {}

  public record MtlsConfig(
      String certificateFile,
      String certificatePassword,
      String certificateType,
      String keyManagerPassword,
      String truststoreFile,
      String truststorePassword,
      String truststoreType) {}
}
