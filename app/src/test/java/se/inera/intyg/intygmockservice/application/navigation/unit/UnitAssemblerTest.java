package se.inera.intyg.intygmockservice.application.navigation.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.inera.intyg.intygmockservice.domain.navigation.model.CareProvider;
import se.inera.intyg.intygmockservice.domain.navigation.model.Unit;

class UnitAssemblerTest {

  private final UnitAssembler assembler = new UnitAssembler();

  @Test
  void toModel_ShouldIncludeSelfLink() {
    final var model = assembler.toModel(unit("unit-001"));

    assertTrue(model.getLink("self").isPresent());
    assertTrue(model.getLink("self").get().getHref().contains("/api/navigate/units/unit-001"));
  }

  @Test
  void toModel_ShouldIncludeCertificatesLink() {
    final var model = assembler.toModel(unit("unit-001"));

    assertTrue(model.getLink("certificates").isPresent());
    assertTrue(
        model
            .getLink("certificates")
            .get()
            .getHref()
            .contains("/api/navigate/units/unit-001/certificates"));
  }

  @Test
  void toModel_ShouldMapAllUnitFields() {
    final var careProvider =
        CareProvider.builder()
            .careProviderId("provider-001")
            .careProviderName("Test Provider")
            .build();
    final var unit =
        Unit.builder()
            .unitId("unit-001")
            .unitName("Test Unit")
            .streetAddress("Testgatan 1")
            .postalCode("12345")
            .city("Teststaden")
            .phone("08-123456")
            .email("test@unit.se")
            .careProvider(careProvider)
            .build();

    final var response = assembler.toModel(unit).getContent();

    assertNotNull(response);
    assertEquals("unit-001", response.unitId());
    assertEquals("Test Unit", response.unitName());
    assertEquals("Testgatan 1", response.streetAddress());
    assertEquals("12345", response.postalCode());
    assertEquals("Teststaden", response.city());
    assertEquals("08-123456", response.phone());
    assertEquals("test@unit.se", response.email());
    assertEquals("provider-001", response.careProviderId());
    assertEquals("Test Provider", response.careProviderName());
  }

  @Test
  void toModel_ShouldReturnNullCareProviderFieldsWhenAbsent() {
    final var response = assembler.toModel(unit("unit-001")).getContent();

    assertNotNull(response);
    assertNull(response.careProviderId());
    assertNull(response.careProviderName());
  }

  @Test
  void toCollectionModel_ShouldIncludeSelfLink() {
    final var model = assembler.toCollectionModel(List.of(unit("unit-001")));

    assertTrue(model.getLink("self").isPresent());
    assertTrue(model.getLink("self").get().getHref().contains("/api/navigate/units"));
  }

  @Test
  void toCollectionModel_ShouldContainAllItems() {
    final var model = assembler.toCollectionModel(List.of(unit("unit-001"), unit("unit-002")));

    assertEquals(2, model.getContent().size());
  }

  private static Unit unit(final String unitId) {
    return Unit.builder().unitId(unitId).build();
  }
}
