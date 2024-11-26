package by.opinio.controller;

import by.opinio.API.ApiResponse;
import by.opinio.domain.AddAnswersDTO;
import by.opinio.domain.PollDto;
import by.opinio.domain.QuestionDto;
import by.opinio.entity.Answer;
import by.opinio.entity.Question;
import by.opinio.repository.AnswerRepository;
import by.opinio.repository.QuestionRepository;
import by.opinio.service.PollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/polls")
@RequiredArgsConstructor
public class PollsController {

    private final PollService pollService;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    /**
     * Создание нового опроса.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PollDto>> createPoll(@RequestBody PollDto pollDto) {
        PollDto createdPoll = pollService.createPoll(pollDto);
        ApiResponse<PollDto> apiResponse = ApiResponse.<PollDto>builder()
                .data(createdPoll)
                .status(true)
                .message("Poll created successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Обновление существующего опроса.
     */
    @PutMapping("/{pollId}")
    public ResponseEntity<ApiResponse<PollDto>> updatePoll(@PathVariable UUID pollId, @RequestBody PollDto pollDto) {
        PollDto updatedPoll = pollService.updatePoll(pollId, pollDto);
        ApiResponse<PollDto> apiResponse = ApiResponse.<PollDto>builder()
                .data(updatedPoll)
                .status(true)
                .message("Pull updated successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Удаление опроса.
     */
    @DeleteMapping("/{pollId}")
    public ResponseEntity<ApiResponse<Void>> deletePoll(@PathVariable UUID pollId) {
        pollService.deletePoll(pollId);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .status(true)
                .message("Poll deleted successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Получение опроса по ID.
     */
    @GetMapping("/{pollId}")
    public ResponseEntity<ApiResponse<PollDto>> getPollById(@PathVariable UUID pollId) {
        PollDto poll = pollService.getPollById(pollId);
        ApiResponse<PollDto> apiResponse = ApiResponse.<PollDto>builder()
                .data(poll)
                .status(true)
                .message("Poll fetched successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Добавление вопросов к опросу.
     */
    @PostMapping("/{pollId}/questions")
    public ResponseEntity<ApiResponse<PollDto>> addQuestionsToPoll(
            @PathVariable UUID pollId,
            @RequestBody List<QuestionDto> questionDtos) {
        PollDto updatedPoll = pollService.addQuestionsToPoll(pollId, questionDtos);
        ApiResponse<PollDto> apiResponse = ApiResponse.<PollDto>builder()
                .data(updatedPoll)
                .status(true)
                .message("Questions added successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }
    @PostMapping("/{questionId}/answers")
    public ResponseEntity<?> addAnswersToQuestion(
            @PathVariable UUID questionId,
            @RequestBody AddAnswersDTO addAnswersDTO) {

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with ID: " + questionId));

        for (int i = 0; i <  addAnswersDTO.getAnswers().size(); i ++) {
            Answer answer = Answer.builder()
                    .id(UUID.randomUUID())
                    .answer(addAnswersDTO.getAnswers().get(i).getAnswer())
                    .submittedAt(LocalDateTime.now())
                    .question(question)
                    .build();

            answerRepository.save(answer);
        }

        return ResponseEntity.ok("Answers added successfully");
    }
    /**
     * Удаление вопроса.
     */
    @DeleteMapping("/{pollId}/questions/{questionId}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestionFromPoll(
            @PathVariable UUID pollId,
            @PathVariable UUID questionId) {
        pollService.deleteQuestionFromPoll(pollId, questionId);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .data(null)
                .status(true)
                .message("Question deleted successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Добавление бонусов к опросу.
     */
    @PostMapping("/{pollId}/bonuses")
    public ResponseEntity<ApiResponse<PollDto>> addBonusesToPoll(
            @PathVariable UUID pollId,
            @RequestBody List<UUID> bonusIds) {
        PollDto updatedPoll = pollService.addBonusesToPoll(pollId, bonusIds);
        ApiResponse<PollDto> apiResponse = ApiResponse.<PollDto>builder()
                .data(updatedPoll)
                .status(true)
                .message("Bonuses added successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Получение вопросов для опроса.
     */
    @GetMapping("/{pollId}/questions")
    public ResponseEntity<ApiResponse<List<QuestionDto>>> getQuestionsByPollId(@PathVariable UUID pollId) {
        List<QuestionDto> questions = pollService.getQuestionsByPollId(pollId);
        ApiResponse<List<QuestionDto>> apiResponse = ApiResponse.<List<QuestionDto>>builder()
                .data(questions)
                .status(true)
                .message("Questions fetched successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Получение всех опросов.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PollDto>>> getAllPolls() {
        List<PollDto> polls = pollService.getAllPolls();
        ApiResponse<List<PollDto>> apiResponse = ApiResponse.<List<PollDto>>builder()
                .data(polls)
                .status(true)
                .message("All polls fetched successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Получение опросов для организации.
     */
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<ApiResponse<List<PollDto>>> getPollsByOrganization(@PathVariable UUID organizationId) {
        List<PollDto> polls = pollService.getPollsByOrganization(organizationId);
        ApiResponse<List<PollDto>> apiResponse = ApiResponse.<List<PollDto>>builder()
                .data(polls)
                .status(true)
                .message("Polls for organization fetched successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Получение опросов по категориям.
     */
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<List<PollDto>>> getPollsByCategories(@RequestBody List<UUID> categoryIds) {
        List<PollDto> polls = pollService.getPollsByCategories(categoryIds);
        ApiResponse<List<PollDto>> apiResponse = ApiResponse.<List<PollDto>>builder()
                .data(polls)
                .status(true)
                .message("Polls by categories fetched successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Получение опросов по категориям и организации.
     */
    @PostMapping("/organization/{organizationId}/categories")
    public ResponseEntity<ApiResponse<List<PollDto>>> getPollsByOrganizationAndCategories(
            @PathVariable UUID organizationId,
            @RequestBody List<UUID> categoryIds) {
        List<PollDto> polls = pollService.getPollsByOrganizationAndCategories(organizationId, categoryIds);
        ApiResponse<List<PollDto>> apiResponse = ApiResponse.<List<PollDto>>builder()
                .data(polls)
                .status(true)
                .message("Polls by organization and categories fetched successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }
    /**
     * Получение новинок опросов.
     */
    @GetMapping("/newPolls")
    public ResponseEntity<List<PollDto>> getNewPolls() {
        List<PollDto> newPolls = pollService.getNewPolls();
        return ResponseEntity.ok(newPolls);
    }

}
