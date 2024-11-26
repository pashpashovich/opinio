package by.opinio.controller;

import by.opinio.API.ApiResponse;
import by.opinio.domain.OrganizationDto;
import by.opinio.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping("/{userId}/subscribe/{organizationId}")
    public ResponseEntity<ApiResponse<Void>> subscribeToOrganization(
            @PathVariable UUID userId, @PathVariable UUID organizationId) {
        subscriptionService.subscribeToOrganization(userId, organizationId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(true)
                .message("Subscription added successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}/unsubscribe/{organizationId}")
    public ResponseEntity<ApiResponse<Void>> unsubscribeFromOrganization(
            @PathVariable UUID userId, @PathVariable UUID organizationId) {
        subscriptionService.unsubscribeFromOrganization(userId, organizationId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(true)
                .message("Unsubscribed successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<OrganizationDto>>> getUserSubscriptions(@PathVariable UUID userId) {
        List<OrganizationDto> subscriptions = subscriptionService.getUserSubscriptions(userId);
        ApiResponse<List<OrganizationDto>> response = ApiResponse.<List<OrganizationDto>>builder()
                .data(subscriptions)
                .status(true)
                .message("User subscriptions fetched successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}

