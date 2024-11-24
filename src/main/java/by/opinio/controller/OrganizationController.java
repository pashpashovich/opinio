package by.opinio.controller;

import by.opinio.domain.CategoryDto;
import by.opinio.domain.OrganizationDto;
import by.opinio.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDto> getOrganizationInfo(@PathVariable UUID id) {
        OrganizationDto organizationDto = organizationService.getOrganizationInfo(id);
        return ResponseEntity.ok(organizationDto);
    }
    /*
    Метод для получения организаций
    по категориям
     */
    @GetMapping("/categories")
    public ResponseEntity<List<OrganizationDto>> getOrganizationsByCategories(@RequestParam List<String> categories) {
        List<OrganizationDto> organizations = organizationService.getOrganizationsByCategories(categories);
        return ResponseEntity.ok(organizations);
    }

    /*
        Метод для получения организаций,
        которые понравились, если таких нет, ничего не возращаем
    */
    @GetMapping("/liked")
    public ResponseEntity<List<OrganizationDto>> getLikedOrganizations(@RequestParam UUID userId) {
        List<OrganizationDto> organizations = organizationService.getLikedOrganizations(userId);
        return ResponseEntity.ok(organizations);
    }
    /*
          Метод для получения организаций, которые могут понравится
          по категориям, если пользователь голый, то возращаем категории
      */
    @GetMapping("/interests")
    public ResponseEntity<?> getOrganizationsByInterests(@RequestParam UUID userId) {
        List<OrganizationDto> organizations = organizationService.getOrganizationsByUserInterests(userId);
        if (organizations.isEmpty()) {
            List<CategoryDto> categories = organizationService.getAllCategories();
            return ResponseEntity.ok(categories);
        }
        return ResponseEntity.ok(organizations);
    }


}