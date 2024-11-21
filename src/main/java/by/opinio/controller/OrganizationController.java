package by.opinio.controller;

import by.opinio.domain.OrganizationDto;
import by.opinio.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}