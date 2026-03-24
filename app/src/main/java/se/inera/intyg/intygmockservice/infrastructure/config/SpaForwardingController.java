package se.inera.intyg.intygmockservice.infrastructure.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardingController {

  @GetMapping(
      value = {
        "/",
        "/{x:[\\w\\-]+}",
        "/{x:[\\w\\-]+}/{y:[\\w\\-]+}",
        "/{x:[\\w\\-]+}/{y:[\\w\\-]+}/{z:[\\w\\-]+}"
      })
  public String forwardToIndex() {
    return "forward:/index.html";
  }
}
