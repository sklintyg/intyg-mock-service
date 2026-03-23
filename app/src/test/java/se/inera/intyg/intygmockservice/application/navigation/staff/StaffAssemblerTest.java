package se.inera.intyg.intygmockservice.application.navigation.staff;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.inera.intyg.intygmockservice.domain.navigation.model.Staff;
import se.inera.intyg.intygmockservice.domain.navigation.model.Unit;

class StaffAssemblerTest {

  private final StaffAssembler assembler = new StaffAssembler();

  @Test
  void toModel_ShouldIncludeSelfLink() {
    final var model = assembler.toModel(staff("staff-001"));

    assertTrue(model.getLink("self").isPresent());
    assertTrue(model.getLink("self").get().getHref().contains("/api/navigate/staff/staff-001"));
  }

  @Test
  void toModel_ShouldIncludeCertificatesLink() {
    final var model = assembler.toModel(staff("staff-001"));

    assertTrue(model.getLink("certificates").isPresent());
    assertTrue(
        model
            .getLink("certificates")
            .get()
            .getHref()
            .contains("/api/navigate/staff/staff-001/certificates"));
  }

  @Test
  void toModel_ShouldMapAllStaffFields() {
    final var unit = Unit.builder().unitId("unit-001").build();
    final var s =
        Staff.builder()
            .staffId("staff-001")
            .fullName("Anna Läkare")
            .prescriptionCode("0000001")
            .unit(unit)
            .build();

    final var response = assembler.toModel(s).getContent();

    assertNotNull(response);
    assertEquals("staff-001", response.staffId());
    assertEquals("Anna Läkare", response.fullName());
    assertEquals("0000001", response.prescriptionCode());
    assertEquals("unit-001", response.unitId());
  }

  @Test
  void toModel_ShouldReturnNullUnitIdWhenUnitAbsent() {
    final var response = assembler.toModel(staff("staff-001")).getContent();

    assertNotNull(response);
    assertNull(response.unitId());
  }

  @Test
  void toCollectionModel_ShouldIncludeSelfLink() {
    final var model = assembler.toCollectionModel(List.of(staff("staff-001")));

    assertTrue(model.getLink("self").isPresent());
    assertTrue(model.getLink("self").get().getHref().contains("/api/navigate/staff"));
  }

  @Test
  void toCollectionModel_ShouldContainAllItems() {
    final var model = assembler.toCollectionModel(List.of(staff("staff-001"), staff("staff-002")));

    assertEquals(2, model.getContent().size());
  }

  private static Staff staff(final String staffId) {
    return Staff.builder().staffId(staffId).build();
  }
}
