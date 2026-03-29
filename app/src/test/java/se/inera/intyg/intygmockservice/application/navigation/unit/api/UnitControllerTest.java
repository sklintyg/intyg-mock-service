package se.inera.intyg.intygmockservice.application.navigation.unit;

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
import se.inera.intyg.intygmockservice.domain.navigation.model.Certificate;
import se.inera.intyg.intygmockservice.domain.navigation.model.Unit;

@ExtendWith(MockitoExtension.class)
class UnitControllerTest {

  @Mock private UnitNavigationService service;
  @Mock private UnitAssembler assembler;
  @Mock private CertificateAssembler certificateAssembler;

  @InjectMocks private UnitController controller;

  @Test
  void getAllUnits_ShouldDelegateToServiceAndAssembler() {
    final var unit = Unit.builder().unitId("unit-001").build();
    final var collectionModel = CollectionModel.<EntityModel<UnitResponse>>empty();

    when(service.findAll()).thenReturn(List.of(unit));
    when(assembler.toCollectionModel(List.of(unit))).thenReturn(collectionModel);

    final var result = controller.getAllUnits();

    assertNotNull(result);
    verify(service).findAll();
    verify(assembler).toCollectionModel(List.of(unit));
  }

  @Test
  void getUnitById_ShouldReturn200WhenFound() {
    final var unit = Unit.builder().unitId("unit-001").build();
    final var entityModel =
        EntityModel.of(
            new UnitResponse("unit-001", null, null, null, null, null, null, null, null));

    when(service.findById("unit-001")).thenReturn(Optional.of(unit));
    when(assembler.toModel(unit)).thenReturn(entityModel);

    final var response = controller.getUnitById("unit-001");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void getUnitById_ShouldReturn404WhenNotFound() {
    when(service.findById("unknown")).thenReturn(Optional.empty());

    final var response = controller.getUnitById("unknown");

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void getUnitCertificates_ShouldDelegateToServiceAndAssembler() {
    final var certificate = Certificate.builder().certificateId("cert-001").build();
    final var collectionModel = CollectionModel.<EntityModel<CertificateResponse>>empty();

    when(service.findCertificatesByUnitId("unit-001")).thenReturn(List.of(certificate));
    when(certificateAssembler.toCollectionModel(List.of(certificate))).thenReturn(collectionModel);

    final var result = controller.getUnitCertificates("unit-001");

    assertNotNull(result);
    verify(service).findCertificatesByUnitId("unit-001");
    verify(certificateAssembler).toCollectionModel(List.of(certificate));
  }
}
