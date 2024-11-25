package by.opinio.service;

import by.opinio.Exception.AppException;
import by.opinio.domain.OrganizationDto;
import by.opinio.entity.Organization;
import by.opinio.entity.User;
import by.opinio.repository.OrganizationRepository;
import by.opinio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final NotificationService notificationService;

    public void subscribeToOrganization(UUID userId, UUID organizationId) {
        User user = (User) userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new AppException("Organization not found", HttpStatus.NOT_FOUND));

        // Добавляем организацию в подписки пользователя
        if (!user.getSubscriptions().contains(organization)) {
            user.getSubscriptions().add(organization);
        }

        // Добавляем пользователя в подписчики организации
        if (!organization.getSubscribers().contains(user)) {
            organization.getSubscribers().add(user);
        }

        // Сохраняем изменения
        userRepository.save(user);
        organizationRepository.save(organization);
    }

    public void notifySubscribers(Organization organization, String message) {
        organization.getSubscribers().forEach(subscriber -> {
            notificationService.createNotification(subscriber, message);
        });
    }

    public void unsubscribeFromOrganization(UUID userId, UUID organizationId) {
        User user = (User) userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new AppException("Organization not found", HttpStatus.NOT_FOUND));

        if (user.getSubscriptions().contains(organization)) {
            user.getSubscriptions().remove(organization);
            userRepository.save(user);
        }
    }

    public List<OrganizationDto> getUserSubscriptions(UUID userId) {
        User user = (User) userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        return user.getSubscriptions().stream()
                .map(this::convertToDto)
                .toList();
    }

    private OrganizationDto convertToDto(Organization organization) {
        return OrganizationDto.builder()
                .id(organization.getId())
                .name(organization.getName())
                .description(organization.getDescription())
                .build();
    }
}

