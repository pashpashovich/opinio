package by.opinio.domain;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CreatePostDto {
    private String title;
    private String content;
    private UUID organizationId;
}

