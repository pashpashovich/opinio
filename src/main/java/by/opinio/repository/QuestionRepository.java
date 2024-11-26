package by.opinio.repository;

import by.opinio.entity.Poll;
import by.opinio.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> findByPoll(Poll poll);
    List<Question> findByPollId(UUID pollId);

}