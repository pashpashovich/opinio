package by.opinio.repository;

import by.opinio.entity.BonusAward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BonusAwardRepository extends JpaRepository<BonusAward, UUID> {}