package by.opinio.domain;

import by.opinio.enums.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {
    private String username;        // Новый логин
    private String password;        // Новый пароль
    private String confirmPassword; // Подтверждение пароля
    private ActivityType activityType; // Род деятельности
    private LocalDate birthDate;    // Дата рождения
}