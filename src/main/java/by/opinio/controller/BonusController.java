package by.opinio.controller;

import by.opinio.API.ApiResponse;
import by.opinio.domain.BonusDto;
import by.opinio.entity.Bonus;
import by.opinio.repository.BonusRepository;
import by.opinio.service.BonusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bonuses")
@RequiredArgsConstructor
public class BonusController {

    private final BonusService bonusService;

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

}
