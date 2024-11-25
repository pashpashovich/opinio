package by.opinio.controller;

import by.opinio.API.ApiResponse;
import by.opinio.domain.BonusDto;
import by.opinio.domain.LikedOrganizationRequest;
import by.opinio.domain.OrganizationDto;
import by.opinio.domain.PopularOrganizationDto;
import by.opinio.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationDto>> getOrganizationInfo(@PathVariable UUID id) {
        OrganizationDto organizationDto = organizationService.getOrganizationInfo(id);
        ApiResponse<OrganizationDto> response = ApiResponse.<OrganizationDto>builder()
                .data(organizationDto)
                .status(true)
                .message("Organization taken successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    /*
    Метод для получения организаций
    по категориям
     */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<OrganizationDto>>> getOrganizationsByUserCategories(@RequestParam UUID userId) {
        List<OrganizationDto> organizations = organizationService.getOrganizationsByUserCategories(userId);
        ApiResponse<List<OrganizationDto>> response = ApiResponse.<List<OrganizationDto>>builder()
                .data(organizations)
                .status(true)
                .message("Organization by user categories taken successfully")
                .build();
        return ResponseEntity.ok(response);
    }
    /*
        Метод для получения организаций,
        которые понравились, если таких нет, ничего не возращаем
    */
    @GetMapping("/liked")
    public ResponseEntity<ApiResponse<List<OrganizationDto>>> getLikedOrganizations(@RequestParam UUID userId) {
        List<OrganizationDto> organizations = organizationService.getLikedOrganizations(userId);
        ApiResponse<List<OrganizationDto>> response = ApiResponse.<List<OrganizationDto>>builder()
                .data(organizations)
                .status(true)
                .message("Organization by user likes taken successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/liked")
    public ResponseEntity<ApiResponse<Void>> addLikedOrganization(@RequestParam UUID userId, @RequestBody LikedOrganizationRequest request) {
        organizationService.addLikedOrganization(userId, request.getOrganizationId());
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(true)
                .message("Organization liked successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    /*
          Метод для получения организаций, которые могут понравится
          по категориям, если пользователь голый, то возращаем категории
    */
    @GetMapping("/interests")
    public ResponseEntity<ApiResponse<List<OrganizationDto>>> getOrganizationsByInterests(@RequestParam UUID userId) {
        List<OrganizationDto> organizations = organizationService.getOrganizationsByUserInterests(userId);
        ApiResponse<List<OrganizationDto>> apiResponse = new ApiResponse<>();
        if (organizations.isEmpty()) {
            List<OrganizationDto> categories = organizationService.getOrganizationsByUserCategories(userId);
                    apiResponse.setData(categories);
                    apiResponse.setStatus(true);
                    apiResponse.setMessage("Organization by user categories taken successfully");
                    return ResponseEntity.ok(apiResponse);
        }
        apiResponse.setData(organizations);
        apiResponse.setStatus(true);
        apiResponse.setMessage("Organization by user interests taken successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/remove-liked")
    public ResponseEntity<ApiResponse<Void>> removeLikedOrganization(@RequestParam UUID userId, @RequestBody LikedOrganizationRequest request) {
        organizationService.removeLikedOrganization(userId, request.getOrganizationId());
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(true)
                .message("Organization disliked successfully")
                .build();
        return ResponseEntity.ok(response);
    }
    /*
    Метод для получения наград от орги
     */
    @GetMapping("/bonuses")
    public ResponseEntity<ApiResponse<List<BonusDto>>> OrganizationsBonuses(@RequestParam UUID organizationId) {
        List<BonusDto> bonuses = organizationService.getOrganizationsBonuses(organizationId);
        ApiResponse<List<BonusDto>> apiResponse = ApiResponse.<List<BonusDto>>builder()
                .data(bonuses)
                .status(false)
                .message("Bonuses taken successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Получение новых организаций.
     */
    @GetMapping("/newOrganizations")
    public ResponseEntity<List<OrganizationDto>> getNewOrganizations() {
        List<OrganizationDto> newOrganizations = organizationService.getNewOrganizations();
        return ResponseEntity.ok(newOrganizations);
    }
    /**
     * Получение 10 самых популярных организаций.
     */
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<PopularOrganizationDto>>> getTopOrganizations() {
        List<PopularOrganizationDto> topOrganizations = organizationService.getTopOrganizations();
        ApiResponse<List<PopularOrganizationDto>> apiResponse = ApiResponse.<List<PopularOrganizationDto>>builder()
                .data(topOrganizations)
                .status(true)
                .message("Top organizations fetched successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

}