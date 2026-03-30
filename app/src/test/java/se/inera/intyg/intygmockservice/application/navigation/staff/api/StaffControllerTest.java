package se.inera.intyg.intygmockservice.application.navigation.staff.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import se.inera.intyg.intygmockservice.application.navigation.certificate.CertificateAssembler;
import se.inera.intyg.intygmockservice.application.navigation.certificate.CertificateResponse;
import se.inera.intyg.intygmockservice.application.navigation.staff.StaffAssembler;
import se.inera.intyg.intygmockservice.application.navigation.staff.StaffNavigationService;
import se.inera.intyg.intygmockservice.application.navigation.staff.StaffResponse;
import se.inera.intyg.intygmockservice.domain.navigation.model.Certificate;
import se.inera.intyg.intygmockservice.domain.navigation.model.Staff;

@ExtendWith(MockitoExtension.class)
class StaffControllerTest {

  @Mock private StaffNavigationService service;
  @Mock private StaffAssembler assembler;
  @Mock private CertificateAssembler certificateAssembler;

  @InjectMocks private StaffController controller;

  @Test
  void getAllStaff_ShouldDelegateToServiceAndAssembler() {
    final var staff = Staff.builder().staffId("staff-001").build();
    final var collectionModel = CollectionModel.<EntityModel<StaffResponse>>empty();

    when(service.findAll()).thenReturn(List.of(staff));
    when(assembler.toCollectionModel(List.of(staff))).thenReturn(collectionModel);

    final var result = controller.getAllStaff();

    assertNotNull(result);
    verify(service).findAll();
    verify(assembler).toCollectionModel(List.of(staff));
  }

  @Test
  void getStaffById_ShouldReturn200WhenFound() {
    final var staff = Staff.builder().staffId("staff-001").build();
    final var entityModel = EntityModel.of(new StaffResponse("staff-001", null, null, null));

    when(service.findById("staff-001")).thenReturn(Optional.of(staff));
    when(assembler.toModel(staff)).thenReturn(entityModel);

    final var response = controller.getStaffById("staff-001");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void getStaffById_ShouldReturn404WhenNotFound() {
    when(service.findById("unknown")).thenReturn(Optional.empty());

    final var response = controller.getStaffById("unknown");

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void getStaffCertificates_ShouldDelegateToServiceAndAssembler() {
    final var certificate = Certificate.builder().certificateId("cert-001").build();
    final var collectionModel = CollectionModel.<EntityModel<CertificateResponse>>empty();

    when(service.findCertificatesByStaffId("staff-001")).thenReturn(List.of(certificate));
    when(certificateAssembler.toCollectionModel(List.of(certificate))).thenReturn(collectionModel);

    final var result = controller.getStaffCertificates("staff-001");

    assertNotNull(result);
    verify(service).findCertificatesByStaffId("staff-001");
    verify(certificateAssembler).toCollectionModel(List.of(certificate));
  }
}
