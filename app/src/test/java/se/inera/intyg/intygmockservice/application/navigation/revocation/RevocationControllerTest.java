package se.inera.intyg.intygmockservice.application.navigation.revocation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import se.inera.intyg.intygmockservice.domain.navigation.model.Revocation;

@ExtendWith(MockitoExtension.class)
class RevocationControllerTest {

  @Mock private RevocationNavigationService service;
  @Mock private RevocationAssembler assembler;

  @InjectMocks private RevocationController controller;

  @Test
  void getCertificateRevocation_ShouldReturn200WhenFound() {
    final var revocation = Revocation.builder().certificateId("cert-001").build();
    final var model =
        EntityModel.of(new RevocationResponse("cert-001", null, null, null, null, null));

    when(service.findByCertificateId("cert-001")).thenReturn(Optional.of(revocation));
    when(assembler.toModel(revocation)).thenReturn(model);

    final var response = controller.getCertificateRevocation("cert-001");

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void getCertificateRevocation_ShouldReturn404WhenNotFound() {
    when(service.findByCertificateId("unknown")).thenReturn(Optional.empty());

    final var response = controller.getCertificateRevocation("unknown");

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }
}
