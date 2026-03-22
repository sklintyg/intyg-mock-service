package se.inera.intyg.intygmockservice.application.navigation.patient;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.domain.navigation.model.Patient;

@Component
public class PatientAssembler {

  public EntityModel<PatientResponse> toModel(final Patient patient) {
    final var response = toResponse(patient);
    final var personId = patient.getPersonId();

    final var self =
        linkTo(methodOn(PatientController.class).getPatientByPersonId(personId)).withSelfRel();
    final var certificates =
        linkTo(methodOn(PatientController.class).getPatientCertificates(personId))
            .withRel("certificates");
    final var messages =
        linkTo(methodOn(PatientController.class).getPatientMessages(personId)).withRel("messages");

    return EntityModel.of(response, self, certificates, messages);
  }

  private PatientResponse toResponse(final Patient patient) {
    return new PatientResponse(
        patient.getPersonId(),
        patient.getFirstName(),
        patient.getLastName(),
        patient.getStreetAddress(),
        patient.getPostalCode(),
        patient.getCity());
  }
}
