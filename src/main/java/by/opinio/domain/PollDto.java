package by.opinio.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PollDto {
    private UUID id; // Уникальный идентификатор опроса
    private String title; // Название опроса
    private String description; // Описание опроса
    private LocalDateTime createdAt; // Время создания опроса
    private LocalDateTime updatedAt; // Время последнего обновления опроса
}