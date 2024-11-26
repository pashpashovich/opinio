package by.opinio.service;

import by.opinio.Exception.AppException;
import by.opinio.domain.BonusDto;
import by.opinio.entity.AbstractUser;
import by.opinio.entity.Bonus;
import by.opinio.repository.BonusAwardsRepository;
import by.opinio.repository.BonusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BonusService {

    private final BonusRepository bonusRepository;
    private final UserService userRepository;
    private final BonusAwardsRepository bonusAwardsRepository;

    public List<Bonus> getBonusesForUser(UUID userId) {
        AbstractUser user = userRepository.findById(userId)
                .orElseThrow(()->new AppException("No such user", HttpStatus.NOT_FOUND));
        return bonusAwardsRepository.findBonusIdByUser(user);
    }
    public BonusDto createBonus(Bonus bonus) {
        bonus.setId(UUID.randomUUID());
        bonus.setCreatedAt(LocalDateTime.now());
        bonus.setUpdatedAt(LocalDateTime.now());
        bonusRepository.save(bonus);
        return BonusDto.builder()
                .id(bonus.getId())
                .name(bonus.getName())
                .description(bonus.getDescription())
                .build();
    }
    public BonusDto getBonusInfo(UUID bonusId) {
            Bonus bonus = bonusRepository.findById(bonusId)
                .orElseThrow(() -> new IllegalArgumentException("Bonus not found with ID: " + bonusId));

            return convertToDto(bonus);
    }

    private BonusDto convertToDto(Bonus bonus) {
        return new BonusDto().builder()
                .id(bonus.getId())
                .name(bonus.getName())
                .description(bonus.getDescription())
                .build();
    }
}
