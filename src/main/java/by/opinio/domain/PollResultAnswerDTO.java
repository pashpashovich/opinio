package by.opinio.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class PollResultAnswerDTO {
    private UUID questionId;
    private UUID answerId;

    public String toJson() {
        return String.format("{\"questionId\":\"%s\", \"answerId\":\"%s\"}", questionId, answerId);
    }
}
