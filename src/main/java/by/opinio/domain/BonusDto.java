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
public class BonusDto {
    private UUID id; // Уникальный идентификатор бонуса
    private String name; // Название бонуса
    private String description; // Описание бонуса
}
