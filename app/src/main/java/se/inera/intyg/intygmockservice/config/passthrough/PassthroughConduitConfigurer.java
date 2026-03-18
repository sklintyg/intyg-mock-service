package se.inera.intyg.intygmockservice.config.passthrough;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.regex.Pattern;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.configuration.security.FiltersType;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transport.http.HTTPConduitConfigurer;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import se.inera.intyg.intygmockservice.config.properties.PassthroughProperties;

@Slf4j
@Profile("mtls-enabled")
@Configuration
@RequiredArgsConstructor
public class PassthroughConduitConfigurer {

  private static final Pattern CONDUIT_PATTERN =
      Pattern.compile(
          "\\{urn:riv:(clinicalprocess:healthcond|insuranceprocess:healthreporting):.*\\.http-conduit");

  private final PassthroughProperties props;

  @Bean
  public HTTPConduitConfigurer passthroughHttpConduitConfigurer() {
    return (name, address, conduit) -> {
      if (name != null && CONDUIT_PATTERN.matcher(name).find()) {
        configureConduit(conduit);
      }
    };
  }

  private void configureConduit(HTTPConduit conduit) {
    configureClientPolicy(conduit);
    configureTls(conduit);
  }

  private void configureClientPolicy(HTTPConduit conduit) {
    final var policy = new HTTPClientPolicy();
    policy.setAllowChunking(false);
    policy.setAutoRedirect(true);
    policy.setConnection(ConnectionType.KEEP_ALIVE);
    conduit.setClient(policy);
  }

  private void configureTls(HTTPConduit conduit) {
    final var tls = new TLSClientParameters();
    tls.setDisableCNCheck(true);

    try {
      final var tls2 = props.mtls();
      final var keyStore =
          loadKeyStore(tls2.certificateFile(), tls2.certificatePassword(), tls2.certificateType());
      final var kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      kmf.init(keyStore, tls2.keyManagerPassword().toCharArray());
      tls.setKeyManagers(kmf.getKeyManagers());

      final var trustStore =
          loadKeyStore(tls2.truststoreFile(), tls2.truststorePassword(), tls2.truststoreType());
      final var tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tmf.init(trustStore);
      tls.setTrustManagers(tmf.getTrustManagers());

    } catch (KeyStoreException
        | IOException
        | CertificateException
        | NoSuchAlgorithmException
        | UnrecoverableKeyException e) {
      throw new IllegalStateException("Failed to configure CXF TLS conduit", e);
    }

    // <sec:cipherSuitesFilter> … </sec:cipherSuitesFilter>
    final var cipherFilter = new FiltersType();
    cipherFilter
        .getInclude()
        .addAll(
            List.of(
                ".*_EXPORT_.*",
                ".*_EXPORT1024_.*",
                ".*_WITH_DES_.*",
                ".*_WITH_AES_.*",
                ".*_WITH_NULL_.*"));
    cipherFilter.getExclude().add(".*_DH_anon_.*");
    tls.setCipherSuitesFilter(cipherFilter);

    conduit.setTlsClientParameters(tls);
  }

  private KeyStore loadKeyStore(String file, String password, String type)
      throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
    final var keyStore = KeyStore.getInstance(type);
    try (final var fis = new FileInputStream(file)) {
      keyStore.load(fis, password.toCharArray());
    }
    return keyStore;
  }
}
