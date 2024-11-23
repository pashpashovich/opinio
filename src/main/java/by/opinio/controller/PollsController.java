package by.opinio.controller;

import by.opinio.domain.PollDto;
import by.opinio.service.PollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<PollDto>> getPollsByOrganization(@PathVariable UUID organizationId) {
        return ResponseEntity.ok(pollService.getPollsByOrganization(organizationId));
    }
}
