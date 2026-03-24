package se.inera.intyg.intygmockservice.application.navigation.certificate;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygmockservice.application.navigation.certificate.CertificateResponse.CareProviderData;
import se.inera.intyg.intygmockservice.application.navigation.certificate.CertificateResponse.PatientData;
import se.inera.intyg.intygmockservice.application.navigation.certificate.CertificateResponse.StaffData;
import se.inera.intyg.intygmockservice.application.navigation.certificate.CertificateResponse.UnitData;
import se.inera.intyg.intygmockservice.application.navigation.revocation.RevocationController;
import se.inera.intyg.intygmockservice.application.navigation.statusupdate.StatusUpdateController;
import se.inera.intyg.intygmockservice.domain.navigation.model.CareProvider;
import se.inera.intyg.intygmockservice.domain.navigation.model.Certificate;
import se.inera.intyg.intygmockservice.domain.navigation.model.PageResult;
import se.inera.intyg.intygmockservice.domain.navigation.model.Patient;
import se.inera.intyg.intygmockservice.domain.navigation.model.Staff;
import se.inera.intyg.intygmockservice.domain.navigation.model.Unit;

@Component
public class CertificateAssembler {

  public EntityModel<CertificateResponse> toModel(final Certificate certificate) {
    final var response = toResponse(certificate);
    final var id = certificate.getCertificateId();

    final var self =
        linkTo(methodOn(CertificateController.class).getCertificateById(id)).withSelfRel();
    final var messages =
        linkTo(methodOn(CertificateController.class).getCertificateMessages(id))
            .withRel("messages");
    final var statusUpdates =
        linkTo(methodOn(StatusUpdateController.class).getCertificateStatusUpdates(id))
            .withRel("status-updates");
    final var logEntries =
        linkTo(methodOn(CertificateController.class).getCertificateLogEntries(id))
            .withRel("log-entries");
    final var revocation =
        linkTo(methodOn(RevocationController.class).getCertificateRevocation(id))
            .withRel("revocation");

    final var xml = Link.of("/api/register-certificate/" + id + "/xml", "xml");

    final var model =
        EntityModel.of(response, self, messages, statusUpdates, logEntries, revocation, xml);

    if (certificate.getPatient() != null && certificate.getPatient().getPersonId() != null) {
      final var patientLink =
          Link.of("/api/navigate/patients/" + certificate.getPatient().getPersonId(), "patient");
      model.add(patientLink);
    }

    if (certificate.getIssuedBy() != null && certificate.getIssuedBy().getUnit() != null) {
      final var unit = certificate.getIssuedBy().getUnit();
      if (unit.getUnitId() != null) {
        model.add(Link.of("/api/navigate/units/" + unit.getUnitId(), "unit"));
      }
    }

    if (certificate.getIssuedBy() != null && certificate.getIssuedBy().getStaffId() != null) {
      model.add(Link.of("/api/navigate/staff/" + certificate.getIssuedBy().getStaffId(), "issuer"));
    }

    return model;
  }

  public CollectionModel<EntityModel<CertificateResponse>> toCollectionModel(
      final List<Certificate> certificates) {
    final var items = certificates.stream().map(this::toModel).toList();
    final var selfLink =
        linkTo(methodOn(CertificateController.class).getAllCertificates(0, 20)).withSelfRel();
    return CollectionModel.of(items, selfLink);
  }

  public PagedModel<EntityModel<CertificateResponse>> toPagedModel(
      final PageResult<Certificate> pageResult) {
    final var items = pageResult.content().stream().map(this::toModel).toList();
    final var metadata =
        new PagedModel.PageMetadata(
            pageResult.size(),
            pageResult.page(),
            pageResult.totalElements(),
            pageResult.totalPages());

    final var selfLink =
        linkTo(
                methodOn(CertificateController.class)
                    .getAllCertificates(pageResult.page(), pageResult.size()))
            .withSelfRel();

    final var model = PagedModel.of(items, metadata, selfLink);

    if (pageResult.hasNext()) {
      model.add(
          linkTo(
                  methodOn(CertificateController.class)
                      .getAllCertificates(pageResult.page() + 1, pageResult.size()))
              .withRel(IanaLinkRelations.NEXT));
    }

    if (pageResult.hasPrevious()) {
      model.add(
          linkTo(
                  methodOn(CertificateController.class)
                      .getAllCertificates(pageResult.page() - 1, pageResult.size()))
              .withRel(IanaLinkRelations.PREV));
    }

    model.add(
        linkTo(methodOn(CertificateController.class).getAllCertificates(0, pageResult.size()))
            .withRel(IanaLinkRelations.FIRST));

    final var lastPage = Math.max(0, (int) pageResult.totalPages() - 1);
    model.add(
        linkTo(
                methodOn(CertificateController.class)
                    .getAllCertificates(lastPage, pageResult.size()))
            .withRel(IanaLinkRelations.LAST));

    return model;
  }

  private CertificateResponse toResponse(final Certificate certificate) {
    return new CertificateResponse(
        certificate.getCertificateId(),
        certificate.getCertificateType(),
        certificate.getCertificateTypeDisplayName(),
        certificate.getSigningTimestamp(),
        certificate.getSentTimestamp(),
        certificate.getVersion(),
        certificate.getLogicalAddress(),
        toPatientData(certificate.getPatient()),
        toStaffData(certificate.getIssuedBy()));
  }

  private PatientData toPatientData(final Patient patient) {
    if (patient == null) {
      return null;
    }
    return new PatientData(
        patient.getPersonId(),
        patient.getFirstName(),
        patient.getLastName(),
        patient.getStreetAddress(),
        patient.getPostalCode(),
        patient.getCity());
  }

  private StaffData toStaffData(final Staff staff) {
    if (staff == null) {
      return null;
    }
    return new StaffData(
        staff.getStaffId(),
        staff.getFullName(),
        staff.getPrescriptionCode(),
        toUnitData(staff.getUnit()));
  }

  private UnitData toUnitData(final Unit unit) {
    if (unit == null) {
      return null;
    }
    return new UnitData(
        unit.getUnitId(),
        unit.getUnitName(),
        unit.getStreetAddress(),
        unit.getPostalCode(),
        unit.getCity(),
        unit.getPhone(),
        unit.getEmail(),
        toCareProviderData(unit.getCareProvider()));
  }

  private CareProviderData toCareProviderData(final CareProvider careProvider) {
    if (careProvider == null) {
      return null;
    }
    return new CareProviderData(
        careProvider.getCareProviderId(), careProvider.getCareProviderName());
  }
}
