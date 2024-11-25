package by.opinio.repository;

import by.opinio.entity.Bonus;
import by.opinio.entity.Organization;
import by.opinio.entity.Poll;
import by.opinio.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BonusRepository extends JpaRepository<Bonus, UUID> {

    List<Bonus> findAllByOrganization(Organization organization);

    @Query("SELECT b FROM Bonus b WHERE b.id = :bonusId")
    Bonus findBonusById(@Param("bonusId") UUID bonusId);

    @Query("SELECT b FROM Bonus b JOIN b.polls p WHERE p.id = :pollId")
    List<Bonus> findAllByPollId(@Param("pollId") UUID pollId);

}