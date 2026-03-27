package se.inera.intyg.intygmockservice.application.navigation.certificate;

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
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import se.inera.intyg.intygmockservice.application.navigation.logentry.LogEntryAssembler;
import se.inera.intyg.intygmockservice.application.navigation.logentry.LogEntryNavigationService;
import se.inera.intyg.intygmockservice.application.navigation.logentry.LogEntryResponse;
import se.inera.intyg.intygmockservice.application.navigation.message.MessageAssembler;
import se.inera.intyg.intygmockservice.application.navigation.message.MessageNavigationService;
import se.inera.intyg.intygmockservice.application.navigation.message.MessageResponse;
import se.inera.intyg.intygmockservice.domain.navigation.model.AuditLogEntry;
import se.inera.intyg.intygmockservice.domain.navigation.model.Certificate;
import se.inera.intyg.intygmockservice.domain.navigation.model.Message;
import se.inera.intyg.intygmockservice.domain.navigation.model.PageResult;

@ExtendWith(MockitoExtension.class)
class CertificateControllerTest {

  @Mock private CertificateNavigationService service;
  @Mock private CertificateAssembler assembler;
  @Mock private MessageNavigationService messageNavigationService;
  @Mock private MessageAssembler messageAssembler;
  @Mock private LogEntryNavigationService logEntryNavigationService;
  @Mock private LogEntryAssembler logEntryAssembler;

  @InjectMocks private CertificateController controller;

  @Test
  void getAllCertificates_ShouldDelegateToServiceAndAssembler() {
    final var certificate = Certificate.builder().certificateId("cert-001").build();
    final var pageResult = new PageResult<>(List.of(certificate), 0, 20, 1);
    final var pagedModel =
        PagedModel.<EntityModel<CertificateResponse>>empty(
            new PagedModel.PageMetadata(20, 0, 1, 1));

    when(service.findAll(0, 20)).thenReturn(pageResult);
    when(assembler.toPagedModel(pageResult)).thenReturn(pagedModel);

    final var result = controller.getAllCertificates(0, 20);

    assertNotNull(result);
    verify(service).findAll(0, 20);
    verify(assembler).toPagedModel(pageResult);
  }

  @Test
  void getCertificateById_ShouldReturn200WithModelWhenFound() {
    final var certificate = Certificate.builder().certificateId("cert-001").build();
    final var entityModel =
        EntityModel.of(
            new CertificateResponse(null, null, null, null, null, null, null, null, null));

    when(service.findById("cert-001")).thenReturn(Optional.of(certificate));
    when(assembler.toModel(certificate)).thenReturn(entityModel);

    final var response = controller.getCertificateById("cert-001");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void getCertificateById_ShouldReturn404WhenNotFound() {
    when(service.findById("cert-unknown")).thenReturn(Optional.empty());

    final var response = controller.getCertificateById("cert-unknown");

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void getCertificateMessages_ShouldDelegateToServiceAndAssembler() {
    final var message = Message.builder().messageId("msg-001").build();
    final var collectionModel = CollectionModel.<EntityModel<MessageResponse>>empty();

    when(messageNavigationService.findByCertificateId("cert-001")).thenReturn(List.of(message));
    when(messageAssembler.toCollectionModel(List.of(message))).thenReturn(collectionModel);

    final var result = controller.getCertificateMessages("cert-001");

    assertNotNull(result);
    verify(messageNavigationService).findByCertificateId("cert-001");
    verify(messageAssembler).toCollectionModel(List.of(message));
  }

  @Test
  void getCertificateLogEntries_ShouldDelegateToServiceAndAssembler() {
    final var entry = AuditLogEntry.builder().logId("it-log-001").certificateId("cert-001").build();
    final var collectionModel = CollectionModel.<EntityModel<LogEntryResponse>>empty();

    when(logEntryNavigationService.findByCertificateId("cert-001")).thenReturn(List.of(entry));
    when(logEntryAssembler.toCollectionModel(List.of(entry))).thenReturn(collectionModel);

    final var result = controller.getCertificateLogEntries("cert-001");

    assertNotNull(result);
    verify(logEntryNavigationService).findByCertificateId("cert-001");
    verify(logEntryAssembler).toCollectionModel(List.of(entry));
  }
}
