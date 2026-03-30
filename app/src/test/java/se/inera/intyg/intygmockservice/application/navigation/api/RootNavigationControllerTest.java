package se.inera.intyg.intygmockservice.application.navigation.api;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;

class RootNavigationControllerTest {

  private final RootNavigationController controller = new RootNavigationController();

  @Test
  void getRoot_ShouldReturnAllExpectedLinks() {
    final var model = controller.getRoot();

    final var expectedRels =
        Set.of(
            "certificates",
            "patients",
            "units",
            "staff",
            "messages",
            "status-updates",
            "log-entries");

    for (final var rel : expectedRels) {
      assertTrue(model.getLink(rel).isPresent(), "Missing expected link rel: " + rel);
    }
  }

  @Test
  void getRoot_ShouldContainNavigatePathInLinks() {
    final var model = controller.getRoot();

    model
        .getLinks()
        .forEach(
            link ->
                assertTrue(
                    link.getHref().contains("/api/navigate"),
                    "Link " + link.getRel() + " should point to /api/navigate"));
  }
}
