package by.opinio.domain;


import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AddAnswersDTO {
    private UUID questionId; // ID вопроса
    private List<String> answers; // Список возможных ответов
}
