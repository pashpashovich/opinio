package by.opinio.service;

import by.opinio.Exception.AppException;
import by.opinio.domain.NotificationDto;
import by.opinio.entity.Notification;
import by.opinio.entity.User;
import by.opinio.repository.NotificationRepository;
import by.opinio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void createNotification(User user, String message) {
        Notification notification = Notification.builder()
                .message("New poll created")
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
    }

    public List<NotificationDto> getNotificationsByUser(UUID userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return notifications.stream()
                .map(notification -> NotificationDto.builder()
                        .id(notification.getId())
                        .message(notification.getMessage())
                        .createdAt(notification.getCreatedAt())
                        .build())
                .toList();
    }
}

