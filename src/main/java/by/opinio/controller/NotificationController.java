package by.opinio.controller;

import by.opinio.API.ApiResponse;
import by.opinio.domain.NotificationDto;
import by.opinio.entity.Notification;
import by.opinio.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getNotificationsByUser(@PathVariable UUID userId) {
        List<NotificationDto> notifications = notificationService.getNotificationsByUser(userId);
        ApiResponse<List<NotificationDto>> apiResponse = ApiResponse.<List<NotificationDto>>builder()
                .data(notifications)
                .status(true)
                .message("Notifications fetched successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}

