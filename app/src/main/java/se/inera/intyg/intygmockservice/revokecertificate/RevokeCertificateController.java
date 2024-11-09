package se.inera.intyg.intygmockservice.revokecertificate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygmockservice.revokecertificate.converter.RevokeCertificateConverter;
import se.inera.intyg.intygmockservice.revokecertificate.dto.RevokeCertificateDTO;
import se.inera.intyg.intygmockservice.revokecertificate.repository.RevokeCertificateRepository;

@RestController
@RequestMapping("/api/revoke-certificate")
@RequiredArgsConstructor
@Tag(name = "RevokeCertificate", description = "API for managing certificate revocations")
public class RevokeCertificateController {

    private final RevokeCertificateRepository repository;
    private final RevokeCertificateConverter revokeCertificateConverter;

    @Operation(summary = "Get all revoke certificates", description = "Retrieve all revoke certificates")
    @GetMapping
    public List<RevokeCertificateDTO> getAllRevokeCertificates() {
        return repository.findAll().stream()
            .map(revokeCertificateConverter::convert)
            .collect(Collectors.toList());
    }

    @Operation(summary = "Delete all revoke certificates", description = "Delete all revoke certificates")
    @DeleteMapping
    public void deleteAllRevokeCertificates() {
        repository.deleteAll();
    }
}