package by.opinio.auth;

import by.opinio.enums.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestOrganization {
    private String login;
    private String password;
    private String name;
    private ActivityType activityType;
    private String[] tags;
}