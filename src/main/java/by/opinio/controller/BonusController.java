package by.opinio.controller;

import by.opinio.API.ApiResponse;
import by.opinio.domain.BonusDto;
import by.opinio.entity.Bonus;
import by.opinio.entity.Organization;
import by.opinio.repository.BonusRepository;
import by.opinio.repository.OrganizationRepository;
import by.opinio.service.BonusService;
import by.opinio.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bonuses")
@RequiredArgsConstructor
public class BonusController {

    private final BonusService bonusService;
    private final OrganizationRepository organizationRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<Bonus>>> getBonuses(@PathVariable UUID userId) {
        List<Bonus> bonuses = bonusService.getBonusesForUser(userId);
        ApiResponse<List<Bonus>> apiResponse = ApiResponse.<List<Bonus>>builder()
                .data(bonuses)
                .status(true)
                .message("Bonuses for user")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/info/{bonusId}")
    public ResponseEntity<ApiResponse<BonusDto>> getBonusInfo(@PathVariable UUID bonusId) {
        BonusDto bonus = bonusService.getBonusInfo(bonusId);
        ApiResponse<BonusDto> apiResponse = ApiResponse.<BonusDto>builder()
                .data(bonus)
                .status(true)
                .message("Bonus information retrieved successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping
    public ResponseEntity<?> createBonus(@RequestBody BonusDto bonusDTO) {

        Optional<Organization> organizationOptional = organizationRepository.findById(bonusDTO.getId());
        if (organizationOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Organization not found");
        }

        Bonus bonus = Bonus.builder()
                .name(bonusDTO.getName())
                .description(bonusDTO.getDescription())
                .organization(organizationOptional.get())
                .build();

        BonusDto savedBonus = bonusService.createBonus(bonus);
        return ResponseEntity.ok(savedBonus);
    }

}
