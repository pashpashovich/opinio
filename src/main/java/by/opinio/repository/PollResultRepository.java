package by.opinio.repository;

import by.opinio.entity.PollResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PollResultRepository extends JpaRepository<PollResult, UUID> {
    @Query("SELECT COUNT(pr) FROM PollResult pr WHERE pr.answer LIKE %:answerId%")
    long countByAnswerContaining(@Param("answerId") UUID answerId);

}