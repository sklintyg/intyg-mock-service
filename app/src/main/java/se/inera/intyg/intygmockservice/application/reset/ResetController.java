package se.inera.intyg.intygmockservice.application.reset;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygmockservice.application.common.behavior.service.BehaviorService;
import se.inera.intyg.intygmockservice.application.registercertificate.RegisterCertificateService;
import se.inera.intyg.intygmockservice.application.revokecertificate.RevokeCertificateService;
import se.inera.intyg.intygmockservice.application.sendmessagetorecipient.SendMessageToRecipientService;
import se.inera.intyg.intygmockservice.application.statusupdates.CertificateStatusUpdateForCareService;
import se.inera.intyg.intygmockservice.application.storelog.StoreLogService;

@Tag(name = "Reset")
@RestController
@RequestMapping("/api/reset")
@RequiredArgsConstructor
public class ResetController {

  private final RegisterCertificateService registerCertificateService;
  private final RevokeCertificateService revokeCertificateService;
  private final SendMessageToRecipientService sendMessageToRecipientService;
  private final CertificateStatusUpdateForCareService certificateStatusUpdateForCareService;
  private final StoreLogService storeLogService;
  private final BehaviorService behaviorService;

  @Operation(summary = "Delete all stored data across all modules")
  @DeleteMapping
  public ResponseEntity<Void> reset() {
    registerCertificateService.deleteAll();
    revokeCertificateService.deleteAll();
    sendMessageToRecipientService.deleteAll();
    certificateStatusUpdateForCareService.deleteAll();
    storeLogService.deleteAll();
    behaviorService.deleteAll();
    return ResponseEntity.noContent().build();
  }
}
