package by.opinio.controller;

import by.opinio.domain.PollDto;
import by.opinio.domain.QuestionDto;
import by.opinio.entity.Question;
import by.opinio.service.PollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/polls")
@RequiredArgsConstructor
public class PollsController {

    private final PollService pollService;

    /**
     * Создание нового опроса.
     */
    @PostMapping
    public ResponseEntity<PollDto> createPoll(@RequestBody PollDto pollDto) {
        PollDto createdPoll = pollService.createPoll(pollDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPoll);
    }

    /**
     * Обновление существующего опроса.
     */
    @PutMapping("/{pollId}")
    public ResponseEntity<PollDto> updatePoll(@PathVariable UUID pollId, @RequestBody PollDto pollDto) {
        PollDto updatedPoll = pollService.updatePoll(pollId, pollDto);
        return ResponseEntity.ok(updatedPoll);
    }

    /**
     * Удаление опроса.
     */
    @DeleteMapping("/{pollId}")
    public ResponseEntity<Void> deletePoll(@PathVariable UUID pollId) {
        pollService.deletePoll(pollId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получение опроса по ID.
     */
    @GetMapping("/{pollId}")
    public ResponseEntity<PollDto> getPollById(@PathVariable UUID pollId) {
        PollDto poll = pollService.getPollById(pollId);
        return ResponseEntity.ok(poll);
    }

    /**
     * Добавление вопросов к опросу.
     */
    @PostMapping("/{pollId}/questions")
    public ResponseEntity<PollDto> addQuestionsToPoll(
            @PathVariable UUID pollId,
            @RequestBody List<QuestionDto> questionDtos) {
        PollDto updatedPoll = pollService.addQuestionsToPoll(pollId, questionDtos);
        return ResponseEntity.ok(updatedPoll);
    }

    /**
     * Удаление вопроса.
     */
    @DeleteMapping("/{pollId}/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestionFromPoll(
            @PathVariable UUID pollId,
            @PathVariable UUID questionId) {
        pollService.deleteQuestionFromPoll(pollId, questionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Добавление бонусов к опросу.
     */
    @PostMapping("/{pollId}/bonuses")
    public ResponseEntity<PollDto> addBonusesToPoll(
            @PathVariable UUID pollId,
            @RequestBody List<UUID> bonusIds) {
        PollDto updatedPoll = pollService.addBonusesToPoll(pollId, bonusIds);
        return ResponseEntity.ok(updatedPoll);
    }

    /**
     * Получение вопросов для опроса.
     */
    @GetMapping("/{pollId}/questions")
    public ResponseEntity<List<QuestionDto>> getQuestionsByPollId(@PathVariable UUID pollId) {
        List<QuestionDto> questions = pollService.getQuestionsByPollId(pollId);
        return ResponseEntity.ok(questions);
    }

    /**
     * Получение всех опросов.
     */
    @GetMapping
    public ResponseEntity<List<PollDto>> getAllPolls() {
        List<PollDto> polls = pollService.getAllPolls();
        return ResponseEntity.ok(polls);
    }

    /**
     * Получение опросов для организации.
     */
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<PollDto>> getPollsByOrganization(@PathVariable UUID organizationId) {
        List<PollDto> polls = pollService.getPollsByOrganization(organizationId);
        return ResponseEntity.ok(polls);
    }

    /**
     * Получение опросов по категориям.
     */
    @PostMapping("/categories")
    public ResponseEntity<List<PollDto>> getPollsByCategories(@RequestBody List<UUID> categoryIds) {
        List<PollDto> polls = pollService.getPollsByCategories(categoryIds);
        return ResponseEntity.ok(polls);
    }

    /**
     * Получение опросов по категориям и организации.
     */
    @PostMapping("/organization/{organizationId}/categories")
    public ResponseEntity<List<PollDto>> getPollsByOrganizationAndCategories(
            @PathVariable UUID organizationId,
            @RequestBody List<UUID> categoryIds) {
        List<PollDto> polls = pollService.getPollsByOrganizationAndCategories(organizationId, categoryIds);
        return ResponseEntity.ok(polls);
    }
}
