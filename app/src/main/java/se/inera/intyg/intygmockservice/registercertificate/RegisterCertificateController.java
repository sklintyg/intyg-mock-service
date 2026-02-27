package se.inera.intyg.intygmockservice.registercertificate;

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
import se.inera.intyg.intygmockservice.registercertificate.dto.RegisterCertificateDTO;

@RestController
@RequestMapping("/api/register-certificate")
@RequiredArgsConstructor
@Tag(name = "RegisterCertificate", description = "API for managing certificate registrations")
public class RegisterCertificateController {

    private final RegisterCertificateService registerCertificateService;

    @Operation(
        summary = "Get all registered certificates",
        description = "Retrieve all registered certificates")
    @GetMapping
    public List<RegisterCertificateDTO> getAllRegisteredCertificates() {
        return registerCertificateService.getAll();
    }

    @Operation(
        summary = "Get registered certificate as XML",
        description = "Retrieve a registered certificate as raw XML by certificate ID")
    @GetMapping(value = "/{certificateId}/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getCertificateAsXml(@PathVariable final String certificateId) {
        return registerCertificateService
            .getAsXml(certificateId)
            .map(xml -> ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(xml))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Delete all registered certificates",
        description = "Delete all registered certificates")
    @DeleteMapping
    public void deleteAllRegisteredCertificates() {
        registerCertificateService.deleteAll();
    }
}
