package by.opinio.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class PollWithQuestionsDTO {
    private UUID pollId;
    private String title;
    private String description;
    private List<QuestionWithAnswersDTO> questions;
}

