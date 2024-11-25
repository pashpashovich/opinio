package by.opinio.repository;

import by.opinio.entity.Answer;
import by.opinio.entity.Bonus;
import by.opinio.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, UUID> {
    List<Answer> findByQuestion(Question question);
    List<Answer> findByQuestionId(UUID questionId);
}
