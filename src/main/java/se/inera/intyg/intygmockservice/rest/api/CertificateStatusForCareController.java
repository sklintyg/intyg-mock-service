package se.inera.intyg.intygmockservice.rest.api;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygmockservice.CertificateStatusUpdateForCareRepository;
import se.inera.intyg.intygmockservice.dto.CertificateStatusUpdateForCareDTO;
import se.inera.intyg.intygmockservice.dto.converter.CertificateStatusUpdateForCareConverter;

@RestController()
@RequestMapping("/api/certificate-status-for-care")
@RequiredArgsConstructor
public class CertificateStatusForCareController {

    private final CertificateStatusUpdateForCareConverter converter;
    private final CertificateStatusUpdateForCareRepository repository;

    @GetMapping
    public List<CertificateStatusUpdateForCareDTO> getAllCertificateStatusUpdates() {
        return repository.findAll().stream()
            .map(converter::convert)
            .toList();
    }
}
