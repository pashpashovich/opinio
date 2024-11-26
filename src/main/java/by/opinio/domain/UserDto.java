package by.opinio.domain;

import by.opinio.enums.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String username;
    private ActivityType activityType;
    private String activityName; // Только для USER
    private LocalDate birthDate; // Только для USER
    private List<UUID> interestedCategories; // Список ID категорий
    private List<UUID> likedOrganizations; // Список ID организацийp
    private List<UUID> subscriptions; // Подписки на организации
}