package by.opinio.controller;

import by.opinio.domain.PollDto;
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

    @PostMapping
    public ResponseEntity<String> createPoll(@RequestBody PollDto pollDto) {
        pollService.createPoll(pollDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Poll created successfully");
    }

    @PutMapping("/{pollId}")
    public ResponseEntity<String> updatePoll(@PathVariable UUID pollId, @RequestBody PollDto pollDto) {
        pollService.updatePoll(pollId, pollDto);
        return ResponseEntity.ok("Poll updated successfully");
    }

    @GetMapping
    public ResponseEntity<List<PollDto>> getAllPolls() {
        return ResponseEntity.ok(pollService.getAllPolls());
    }

    /**
     * Получение опросов по организации.
     */
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<PollDto>> getPollsByOrganization(@PathVariable UUID organizationId) {
        return ResponseEntity.ok(pollService.getPollsByOrganization(organizationId));
    }
    /**
     * Получение опросов по нескольким категориям.
     */
    @GetMapping("/categories")
    public ResponseEntity<List<PollDto>> getPollsByCategories(@RequestParam List<UUID> categoryIds) {
        return ResponseEntity.ok(pollService.getPollsByCategories(categoryIds));
    }

    /**
     * Получение вопросов по ID опроса.
     */
    @GetMapping("/{pollId}/questions")
    public ResponseEntity<List<Question>> getQuestionsByPollId(@PathVariable UUID pollId) {
        return ResponseEntity.ok(pollService.getQuestionsByPollId(pollId));
    }

    /**
     * Получение опроса по ID.
     */
    @GetMapping("/{pollId}")
    public ResponseEntity<PollDto> getPollById(@PathVariable UUID pollId) {
        return ResponseEntity.ok(pollService.getPollById(pollId));
    }

    /**
     * Удаление опроса по ID.
     */
    @DeleteMapping("/{pollId}")
    public ResponseEntity<String> deletePoll(@PathVariable UUID pollId) {
        pollService.deletePoll(pollId);
        return ResponseEntity.ok("Poll deleted successfully");
    }
    /**
     * Получение опросов по категориям для определённой организации.
     */
    @GetMapping("/organization/{organizationId}/categories")
    public ResponseEntity<List<PollDto>> getPollsByOrganizationAndCategories(
            @PathVariable UUID organizationId,
            @RequestParam List<UUID> categoryIds) {
        return ResponseEntity.ok(pollService.getPollsByOrganizationAndCategories(organizationId, categoryIds));
    }
}
