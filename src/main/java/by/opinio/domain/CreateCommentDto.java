package by.opinio.domain;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateCommentDto {
    private String content;
    private UUID postId;
    private UUID userId;
}

