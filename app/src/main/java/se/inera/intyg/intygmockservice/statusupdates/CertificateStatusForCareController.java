package se.inera.intyg.intygmockservice.statusupdates;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygmockservice.statusupdates.converter.CertificateStatusUpdateForCareConverter;
import se.inera.intyg.intygmockservice.statusupdates.dto.CertificateStatusUpdateForCareDTO;
import se.inera.intyg.intygmockservice.statusupdates.repository.CertificateStatusUpdateForCareRepository;

@RestController()
@RequestMapping("/api/certificate-status-for-care")
@RequiredArgsConstructor
@Tag(name = "CertificateStatusForCare", description = "API for managing certificate status updates for care")
public class CertificateStatusForCareController {

    private final CertificateStatusUpdateForCareConverter converter;
    private final CertificateStatusUpdateForCareRepository repository;

    @Operation(summary = "Get all certificate status updates", description = "Retrieve all certificate status updates for care")
    @GetMapping
    public List<CertificateStatusUpdateForCareDTO> getAllCertificateStatusUpdates() {
        return repository.findAll().stream()
            .map(converter::convert)
            .toList();
    }

    @Operation(summary = "Delete all certificate status updates", description = "Delete all certificate status updates for care")
    @DeleteMapping
    public void deleteAllCertificateStatusUpdates() {
        repository.deleteAll();
    }
}
