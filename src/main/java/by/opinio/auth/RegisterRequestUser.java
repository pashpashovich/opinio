package by.opinio.auth;

import by.opinio.enums.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestUser {
    private String login;
    private String password;
    private ActivityType activityType;
    private String activityName;
    private LocalDate dateOfBirth;
    private String[] tags;
}
