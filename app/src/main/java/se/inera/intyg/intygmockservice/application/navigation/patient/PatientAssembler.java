package se.inera.intyg.intygmockservice.application.navigation.patient;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.domain.navigation.model.Patient;

@Component
public class PatientAssembler {

  public EntityModel<PatientResponse> toModel(final Patient patient) {
    final var response = toResponse(patient);
    final var personId = patient.getPersonId().normalized();

    final var self =
        linkTo(methodOn(PatientController.class).getPatientByPersonId(personId)).withSelfRel();
    final var certificates =
        linkTo(methodOn(PatientController.class).getPatientCertificates(personId))
            .withRel("certificates");
    final var messages =
        linkTo(methodOn(PatientController.class).getPatientMessages(personId)).withRel("messages");

    return EntityModel.of(response, self, certificates, messages);
  }

  public CollectionModel<EntityModel<PatientResponse>> toCollectionModel(
      final List<Patient> patients) {
    final var items = patients.stream().map(this::toModel).toList();
    final var selfLink = linkTo(methodOn(PatientController.class).getAllPatients()).withSelfRel();
    return CollectionModel.of(items, selfLink);
  }

  private PatientResponse toResponse(final Patient patient) {
    return new PatientResponse(
        patient.getPersonId().normalized(),
        patient.getFirstName(),
        patient.getLastName(),
        patient.getStreetAddress(),
        patient.getPostalCode(),
        patient.getCity());
  }
}
