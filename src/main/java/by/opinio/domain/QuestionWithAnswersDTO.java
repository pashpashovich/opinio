package by.opinio.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class QuestionWithAnswersDTO {
    private UUID questionId;
    private String question;
    private List<AnswerDTO> answers;
}

