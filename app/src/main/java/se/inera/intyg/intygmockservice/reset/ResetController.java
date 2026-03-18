package se.inera.intyg.intygmockservice.reset;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygmockservice.registercertificate.RegisterCertificateService;
import se.inera.intyg.intygmockservice.revokecertificate.RevokeCertificateService;
import se.inera.intyg.intygmockservice.sendmessagetorecipient.SendMessageToRecipientService;
import se.inera.intyg.intygmockservice.statusupdates.CertificateStatusUpdateForCareService;
import se.inera.intyg.intygmockservice.storelog.StoreLogService;

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

  @Operation(summary = "Delete all stored data across all modules")
  @DeleteMapping
  public ResponseEntity<Void> reset() {
    registerCertificateService.deleteAll();
    revokeCertificateService.deleteAll();
    sendMessageToRecipientService.deleteAll();
    certificateStatusUpdateForCareService.deleteAll();
    storeLogService.deleteAll();
    return ResponseEntity.noContent().build();
  }
}
