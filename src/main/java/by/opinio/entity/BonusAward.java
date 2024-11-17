package by.opinio.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bonus_awards",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "bonus_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonusAward {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "bonus_id", nullable = false)
    private Bonus bonus;

    private LocalDateTime awardedAt = LocalDateTime.now();
}

