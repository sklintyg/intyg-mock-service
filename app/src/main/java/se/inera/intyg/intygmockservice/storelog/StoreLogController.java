package se.inera.intyg.intygmockservice.storelog;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygmockservice.storelog.converter.StoreLogTypeConverter;
import se.inera.intyg.intygmockservice.storelog.dto.LogTypeDTO;
import se.inera.intyg.intygmockservice.storelog.repository.StoreLogTypeRepository;

@RestController
@Tag(name = "StoreLog", description = "API for managing store logs")
@RequiredArgsConstructor
public class StoreLogController {

    private final StoreLogTypeRepository storeLogTypeRepository;
    private final StoreLogTypeConverter storeLogTypeConverter;

    @Operation(summary = "Get all store logs", description = "Retrieve a list of all store logs")
    @GetMapping
    public List<LogTypeDTO> getAllStoreLogs() {
        return storeLogTypeRepository.findAll().stream()
            .map(storeLogTypeConverter::convertToLogTypeDTO)
            .flatMap(List::stream)
            .toList();
    }

    @Operation(summary = "Delete all store logs", description = "Delete all store logs from the repository")
    @DeleteMapping
    public void deleteAllStoreLogs() {
        storeLogTypeRepository.deleteAll();
    }

    // new endpoint that returns logs related to a specific user
    @Operation(summary = "Get all store logs for a specific user", description = "Retrieve a list of all store logs for a specific user")
    @GetMapping("/user/{userId}")
    public List<LogTypeDTO> getStoreLogsByUserId(String userId) {
        return storeLogTypeRepository.findByUserId(userId).stream()
            .map(storeLogTypeConverter::convertToLogTypeDTO)
            .flatMap(List::stream)
            .toList();
    }
}