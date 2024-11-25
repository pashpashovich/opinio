package by.opinio.domain;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnsweDto {
private UUID id;
private String answer;
private UUID questionId;
private UUID pollId;
private UUID userId;
private String submittedAt; // Для удобства, можно использовать формат даты-времени
}