package by.opinio.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "abstract_users")
@DiscriminatorValue("ORGANIZATION")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Organization extends AbstractUser {

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    private List<Bonus> bonuses;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Poll> polls;

    private String description;

}

