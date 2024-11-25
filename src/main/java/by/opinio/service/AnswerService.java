package by.opinio.service;

import by.opinio.ApiResponse;
import by.opinio.Exception.AppException;
import by.opinio.domain.AnswerDto;
import by.opinio.entity.*;
import by.opinio.repository.AnswerRepository;
import by.opinio.repository.PollRepository;
import by.opinio.repository.QuestionRepository;
import by.opinio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final QuestionRepository questionRepository;
    private final PollRepository pollRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;

    public List<AnswerDto> getAnswersByQuestion(UUID questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new AppException("Question not found", HttpStatus.NOT_FOUND));

        return answerRepository.findByQuestion(question).stream()
                .map(this::convertToDto)
                .toList();
    }


    public AnswerDto saveAnswer(AnswerDto answerDto) {

        Question question = questionRepository.findById(answerDto.getQuestionId())
                .orElseThrow(() -> new AppException("Question not found", HttpStatus.NOT_FOUND));
        Poll poll = pollRepository.findById(answerDto.getPollId())
                .orElseThrow(() -> new AppException("Poll not found", HttpStatus.NOT_FOUND));
        AbstractUser user = null;
        if (answerDto.getUserId() != null) {
            user = userRepository.findById(answerDto.getUserId())
                    .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        }

        Answer answer = Answer.builder()
                .answer(answerDto.getAnswer())
                .question(question)
                .poll(poll)
                .user(user)
                .submittedAt(LocalDateTime.now())
                .build();

        answerRepository.save(answer);
        return convertToDto(answer);
    }
    private AnswerDto convertToDto(Answer answer) {
        return AnswerDto.builder()
                .id(answer.getId())
                .answer(answer.getAnswer())
                .questionId(answer.getQuestion().getId())
                .pollId(answer.getPoll().getId())
                .userId(answer.getUser() != null ? answer.getUser().getId() : null)
                .submittedAt(answer.getSubmittedAt().toString())
                .build();
    }


}
