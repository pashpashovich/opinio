package by.opinio.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PostCommentDto {
    private UUID id;
    private String content;
    private LocalDateTime createdAt;
    private String username;
}

