package by.opinio.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class AnswerDTO {
    private UUID answerId;
    private String answer;
}
