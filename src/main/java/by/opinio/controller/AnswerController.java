package by.opinio.controller;

import by.opinio.domain.AnswerDto;
import by.opinio.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping
    public ResponseEntity<AnswerDto> saveAnswer(@RequestBody AnswerDto answerDto) {
        AnswerDto savedAnswer = answerService.saveAnswer(answerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAnswer);
    }

    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<AnswerDto>> getAnswersByQuestion(@PathVariable UUID questionId) {
        List<AnswerDto> answers = answerService.getAnswersByQuestion(questionId);
        return ResponseEntity.ok(answers);
    }
}
