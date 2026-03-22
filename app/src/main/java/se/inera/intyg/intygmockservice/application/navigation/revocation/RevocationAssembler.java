package se.inera.intyg.intygmockservice.application.navigation.revocation;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.domain.navigation.model.Revocation;

@Component
public class RevocationAssembler {

  public EntityModel<RevocationResponse> toModel(final Revocation revocation) {
    final var response = toResponse(revocation);
    final var id = revocation.getCertificateId();

    final var self =
        linkTo(methodOn(RevocationController.class).getCertificateRevocation(id)).withSelfRel();
    final var model = EntityModel.of(response, self);

    if (id != null) {
      model.add(Link.of("/api/navigate/certificates/" + id, "certificate"));
      model.add(Link.of("/api/revoke-certificate/" + id + "/xml", "xml"));
    }

    if (revocation.getPersonId() != null) {
      model.add(Link.of("/api/navigate/patients/" + revocation.getPersonId(), "patient"));
    }

    return model;
  }

  private RevocationResponse toResponse(final Revocation revocation) {
    return new RevocationResponse(
        revocation.getCertificateId(),
        revocation.getPersonId(),
        revocation.getRevokedAt(),
        revocation.getReason(),
        revocation.getRevokedByStaffId(),
        revocation.getRevokedByFullName());
  }
}
