package by.opinio.repository;

import by.opinio.entity.PollResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PollResultRepository extends JpaRepository<PollResult, UUID> {}