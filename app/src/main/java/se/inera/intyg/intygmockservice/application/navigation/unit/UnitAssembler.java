package se.inera.intyg.intygmockservice.application.navigation.unit;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.application.navigation.unit.api.UnitController;
import se.inera.intyg.intygmockservice.domain.navigation.model.Unit;

@Component
public class UnitAssembler {

  public EntityModel<UnitResponse> toModel(final Unit unit) {
    final var response = toResponse(unit);
    final var unitId = unit.getUnitId();

    final var self = linkTo(methodOn(UnitController.class).getUnitById(unitId)).withSelfRel();
    final var certificates =
        linkTo(methodOn(UnitController.class).getUnitCertificates(unitId)).withRel("certificates");

    return EntityModel.of(response, self, certificates);
  }

  public CollectionModel<EntityModel<UnitResponse>> toCollectionModel(final List<Unit> units) {
    final var items = units.stream().map(this::toModel).toList();
    final var selfLink = linkTo(methodOn(UnitController.class).getAllUnits()).withSelfRel();
    return CollectionModel.of(items, selfLink);
  }

  private UnitResponse toResponse(final Unit unit) {
    return new UnitResponse(
        unit.getUnitId(),
        unit.getUnitName(),
        unit.getStreetAddress(),
        unit.getPostalCode(),
        unit.getCity(),
        unit.getPhone(),
        unit.getEmail(),
        unit.getCareProvider() != null ? unit.getCareProvider().getCareProviderId() : null,
        unit.getCareProvider() != null ? unit.getCareProvider().getCareProviderName() : null);
  }
}
