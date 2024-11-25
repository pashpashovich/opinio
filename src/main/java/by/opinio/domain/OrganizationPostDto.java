package by.opinio.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class OrganizationPostDto {
    private UUID id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String organizationName;
    private int commentCount;
}

