package se.inera.intyg.intygmockservice.application.navigation.api;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygmockservice.application.navigation.certificate.api.CertificateController;
import se.inera.intyg.intygmockservice.application.navigation.logentry.api.LogEntryController;
import se.inera.intyg.intygmockservice.application.navigation.message.api.MessageController;
import se.inera.intyg.intygmockservice.application.navigation.patient.api.PatientController;
import se.inera.intyg.intygmockservice.application.navigation.staff.api.StaffController;
import se.inera.intyg.intygmockservice.application.navigation.statusupdate.api.StatusUpdateController;
import se.inera.intyg.intygmockservice.application.navigation.unit.api.UnitController;

@RestController
@RequestMapping("/api/navigate")
@Tag(name = "Navigate — Root", description = "HATEOAS navigation API entry point")
public class RootNavigationController {

  @Operation(summary = "Discover all available navigation resources")
  @GetMapping
  public RepresentationModel<?> getRoot() {
    final var model = new RepresentationModel<>();

    model.add(
        linkTo(methodOn(CertificateController.class).getAllCertificates(0, 20))
            .withRel("certificates"));
    model.add(linkTo(methodOn(PatientController.class).getAllPatients()).withRel("patients"));
    model.add(linkTo(methodOn(UnitController.class).getAllUnits()).withRel("units"));
    model.add(linkTo(methodOn(StaffController.class).getAllStaff()).withRel("staff"));
    model.add(linkTo(methodOn(MessageController.class).getAllMessages()).withRel("messages"));
    model.add(
        linkTo(methodOn(StatusUpdateController.class).getAllStatusUpdates())
            .withRel("status-updates"));
    model.add(linkTo(methodOn(LogEntryController.class).getAllLogEntries()).withRel("log-entries"));

    return model;
  }
}
