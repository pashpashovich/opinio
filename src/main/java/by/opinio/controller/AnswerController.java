package by.opinio.controller;


import by.opinio.domain.PollResultDTO;
import by.opinio.domain.PollWithQuestionsDTO;
import by.opinio.service.AnswerService;
import by.opinio.service.PollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;
    private final PollService pollService;

    @GetMapping("/polls/{pollId}")
    public ResponseEntity<PollWithQuestionsDTO> getPollDetails(@PathVariable UUID pollId) {
        PollWithQuestionsDTO poll = pollService.getPollDetails(pollId);
        return ResponseEntity.ok(poll);
    }

    @PostMapping("/polls/submit")
    public ResponseEntity<?> submitPollAnswers(@RequestBody PollResultDTO pollResult) {
        pollService.submitPollAnswers(pollResult);
        return ResponseEntity.ok().build();
    }

}
