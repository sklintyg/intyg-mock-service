package se.inera.intyg.intygmockservice.application.storelog;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygmockservice.application.common.dto.CountResponse;
import se.inera.intyg.intygmockservice.application.storelog.dto.LogTypeDTO;
import se.inera.intyg.intygmockservice.application.storelog.service.StoreLogService;

@RestController
@RequestMapping("/api/store-log")
@Tag(name = "Mock — StoreLog", description = "API for managing store logs")
@RequiredArgsConstructor
public class StoreLogController {

  private final StoreLogService storeLogService;

  @Operation(
      summary = "Get all store logs",
      description = "Retrieve a list of all stored audit log entries")
  @GetMapping
  public List<LogTypeDTO> getAllStoreLogs() {
    return storeLogService.getAll();
  }

  @Operation(
      summary = "Get store logs by user",
      description = "Retrieve all audit log entries for a specific user ID")
  @GetMapping("/user/{userId}")
  public List<LogTypeDTO> getStoreLogsByUserId(@PathVariable String userId) {
    return storeLogService.getByUserId(userId);
  }

  @Operation(
      summary = "Get store logs by certificate",
      description =
          "Retrieve all audit log entries for a specific certificate ID (matched against activityLevel)")
  @GetMapping("/certificate/{certificateId}")
  public List<LogTypeDTO> getStoreLogsByCertificateId(@PathVariable String certificateId) {
    return storeLogService.getByCertificateId(certificateId);
  }

  @Operation(
      summary = "Get store log entry as XML",
      description =
          "Retrieve the raw StoreLog SOAP request that contains the given log entry ID as XML")
  @GetMapping(value = "/{logId}/xml", produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<String> getStoreLogAsXml(@PathVariable final String logId) {
    return storeLogService
        .getAsXml(logId)
        .map(xml -> ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(xml))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(summary = "Get count of stored store-log calls")
  @GetMapping("/count")
  public ResponseEntity<CountResponse> getCount() {
    return ResponseEntity.ok(new CountResponse(storeLogService.getCount()));
  }

  @Operation(
      summary = "Delete all store logs",
      description = "Delete all store log entries from the repository")
  @DeleteMapping
  public void deleteAllStoreLogs() {
    storeLogService.deleteAll();
  }

  @Operation(
      summary = "Delete store logs by user",
      description = "Delete all audit log entries associated with a specific user ID")
  @DeleteMapping("/user/{userId}")
  public void deleteStoreLogsByUserId(@PathVariable String userId) {
    storeLogService.deleteByUserId(userId);
  }

  @Operation(
      summary = "Delete store logs by certificate",
      description =
          "Delete all audit log entries associated with a specific certificate ID (matched against activityLevel)")
  @DeleteMapping("/certificate/{certificateId}")
  public void deleteStoreLogsByCertificateId(@PathVariable String certificateId) {
    storeLogService.deleteByCertificateId(certificateId);
  }
}
