package se.inera.intyg.intygmockservice.application.navigation.statusupdate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import se.inera.intyg.intygmockservice.domain.navigation.model.StatusUpdate;

@ExtendWith(MockitoExtension.class)
class StatusUpdateControllerTest {

  @Mock private StatusUpdateNavigationService service;
  @Mock private StatusUpdateAssembler assembler;

  @InjectMocks private StatusUpdateController controller;

  @Test
  void getAllStatusUpdates_ShouldReturnCollectionFromAssembler() {
    final var updates = List.of(StatusUpdate.builder().certificateId("cert-001").build());
    final var expected = CollectionModel.<EntityModel<StatusUpdateResponse>>empty();

    when(service.findAll()).thenReturn(updates);
    when(assembler.toCollectionModel(updates)).thenReturn(expected);

    assertEquals(expected, controller.getAllStatusUpdates());
  }

  @Test
  void getCertificateStatusUpdates_ShouldReturnCollectionFromAssembler() {
    final var updates = List.of(StatusUpdate.builder().certificateId("cert-001").build());
    final var expected = CollectionModel.<EntityModel<StatusUpdateResponse>>empty();

    when(service.findByCertificateId("cert-001")).thenReturn(updates);
    when(assembler.toCollectionModel(updates)).thenReturn(expected);

    assertEquals(expected, controller.getCertificateStatusUpdates("cert-001"));
  }

  @Test
  void getCertificateStatusUpdates_ShouldReturnEmptyCollectionWhenNoUpdates() {
    final var expected = CollectionModel.<EntityModel<StatusUpdateResponse>>empty();

    when(service.findByCertificateId("unknown")).thenReturn(List.of());
    when(assembler.toCollectionModel(List.of())).thenReturn(expected);

    assertEquals(expected, controller.getCertificateStatusUpdates("unknown"));
  }
}
