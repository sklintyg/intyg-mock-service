package se.inera.intyg.intygmockservice.registercertificate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygmockservice.registercertificate.converter.RegisterCertificateConverter;
import se.inera.intyg.intygmockservice.registercertificate.dto.RegisterCertificateDTO;
import se.inera.intyg.intygmockservice.registercertificate.repository.RegisterCertificateRepository;

@RestController
@RequestMapping("/api/register-certificate")
@RequiredArgsConstructor
@Tag(name = "RegisterCertificate", description = "API for managing certificate registrations")
public class RegisterCertificateController {

    private final RegisterCertificateConverter converter;
    private final RegisterCertificateRepository repository;

    @Operation(summary = "Get all registered certificates", description = "Retrieve all registered certificates")
    @GetMapping
    public List<RegisterCertificateDTO> getAllRegisteredCertificates() {
        return repository.findAll().stream()
            .map(converter::convert)
            .toList();
    }

    @Operation(summary = "Delete all registered certificates", description = "Delete all registered certificates")
    @DeleteMapping
    public void deleteAllRegisteredCertificates() {
        repository.deleteAll();
    }
}