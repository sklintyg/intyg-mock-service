package se.inera.intyg.intygmockservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class IntygMockServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(IntygMockServiceApplication.class, args);
  }
}
