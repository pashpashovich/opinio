package by.opinio.repository;

import by.opinio.entity.Bonus;
import by.opinio.entity.Poll;
import by.opinio.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BonusRepository extends JpaRepository<Bonus, UUID> {

}