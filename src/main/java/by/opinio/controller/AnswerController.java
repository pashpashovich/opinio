package by.opinio.controller;


import by.opinio.domain.PollResultDTO;
import by.opinio.domain.PollWithQuestionsDTO;
import by.opinio.entity.Question;
import by.opinio.entity.Answer;
import by.opinio.repository.AnswerRepository;
import by.opinio.repository.QuestionRepository;
import by.opinio.service.AnswerService;
import by.opinio.service.PollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;
    private final PollService pollService;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

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

    @GetMapping("/{pollId}/questions-with-answers")
    public ResponseEntity<Map<String, List<String>>> getQuestionsWithAnswers(@PathVariable UUID pollId) {
        // Получаем все вопросы по pollId
        List<Question> questions = questionRepository.findByPollId(pollId);
        Map<String, List<String>> result = new HashMap<>();

        for (Question question : questions) {
            // Получаем ответы на каждый вопрос
            List<String> answers = answerRepository.findByQuestionId(question.getId())
                    .stream()
                    .map(Answer::getAnswer) // Извлекаем текст ответа
                    .toList();
            result.put(question.getQuestion(), answers); // Добавляем в мапу
        }
        return ResponseEntity.ok(result);
    }


    @GetMapping("/{questionId}/answers-stats")
    public ResponseEntity<Map<String, Long>> getAnswersStats(@PathVariable UUID questionId) {
        // Группируем ответы по тексту и считаем их количество
        List<Object[]> stats = answerRepository.countAnswersGroupedByAnswer(questionId);
        Map<String, Long> result = stats.stream()
                .collect(Collectors.toMap(
                        stat -> (String) stat[0],  // Текст ответа
                        stat -> (Long) stat[1]    // Количество ответов
                ));
        return ResponseEntity.ok(result);
    }



}
