package by.opinio.controller;

import by.opinio.API.ApiResponse;
import by.opinio.domain.AnswerDto;
import by.opinio.service.AnswerService;
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

    @PostMapping
    public ResponseEntity<ApiResponse<AnswerDto>> saveAnswer(@RequestBody AnswerDto answerDto) {
        AnswerDto savedAnswer = answerService.saveAnswer(answerDto);

        ApiResponse<AnswerDto> answerDtoApiResponse = ApiResponse.<AnswerDto>builder()
                .data(savedAnswer)
                .status(true)
                .message("Answer saved successfully ")
                .build();
        return ResponseEntity.ok(answerDtoApiResponse);
    }

    @GetMapping("/question/{questionId}")
    public ResponseEntity<ApiResponse<List<AnswerDto>>> getAnswersByQuestion(@PathVariable UUID questionId) {
        List<AnswerDto> answers = answerService.getAnswersByQuestion(questionId);
        ApiResponse<List<AnswerDto>> apiResponse = ApiResponse.<List<AnswerDto>>builder()
                .data(answers)
                .status(true)
                .message("Answers returned successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
