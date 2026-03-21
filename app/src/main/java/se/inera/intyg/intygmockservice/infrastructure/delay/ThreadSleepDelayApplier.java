package se.inera.intyg.intygmockservice.infrastructure.delay;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.domain.behavior.service.DelayApplier;

@Component
@Slf4j
public class ThreadSleepDelayApplier implements DelayApplier {

  @Override
  public void apply(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.atWarn().setMessage("Delay interrupted").addKeyValue("delay.millis", millis).log();
    }
  }
}
