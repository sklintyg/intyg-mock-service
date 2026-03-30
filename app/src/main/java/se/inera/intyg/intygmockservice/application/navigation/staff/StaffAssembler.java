package se.inera.intyg.intygmockservice.application.navigation.staff;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.application.navigation.staff.api.StaffController;
import se.inera.intyg.intygmockservice.domain.navigation.model.Staff;

@Component
public class StaffAssembler {

  public EntityModel<StaffResponse> toModel(final Staff staff) {
    final var response = toResponse(staff);
    final var staffId = staff.getStaffId();

    final var self = linkTo(methodOn(StaffController.class).getStaffById(staffId)).withSelfRel();
    final var certificates =
        linkTo(methodOn(StaffController.class).getStaffCertificates(staffId))
            .withRel("certificates");

    return EntityModel.of(response, self, certificates);
  }

  public CollectionModel<EntityModel<StaffResponse>> toCollectionModel(
      final List<Staff> staffList) {
    final var items = staffList.stream().map(this::toModel).toList();
    final var selfLink = linkTo(methodOn(StaffController.class).getAllStaff()).withSelfRel();
    return CollectionModel.of(items, selfLink);
  }

  private StaffResponse toResponse(final Staff staff) {
    return new StaffResponse(
        staff.getStaffId(),
        staff.getFullName(),
        staff.getPrescriptionCode(),
        staff.getUnit() != null ? staff.getUnit().getUnitId() : null);
  }
}
