package by.opinio.repository;

import by.opinio.entity.AbstractUser;
import by.opinio.entity.Bonus;
import by.opinio.entity.BonusAward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

@Repository
public interface BonusAwardsRepository extends JpaRepository<BonusAward, UUID> {

    List<BonusAward> findAllByUser(AbstractUser user);

    @Query("SELECT b.bonus FROM BonusAward b WHERE b.user = :user")
    List<Bonus> findBonusIdByUser(AbstractUser user);
}
