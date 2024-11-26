package by.opinio.domain;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddAnswersDTO {
    private List<AnswerDTO> answers; // Список возможных ответов
}
