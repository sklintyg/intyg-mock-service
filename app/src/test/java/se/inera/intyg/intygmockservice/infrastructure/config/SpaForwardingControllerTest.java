package se.inera.intyg.intygmockservice.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SpaForwardingControllerTest {

  private final SpaForwardingController controller = new SpaForwardingController();

  @Test
  void forwardToIndex_ShouldReturnForwardToIndexHtml() {
    assertEquals("forward:/index.html", controller.forwardToIndex());
  }
}
