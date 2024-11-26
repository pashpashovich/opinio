package by.opinio.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class PollResultDTO {
    private UUID pollId;
    private UUID userId;
    private List<PollResultAnswerDTO> answers;
}
