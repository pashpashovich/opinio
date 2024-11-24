package by.opinio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "abstract_users")
@DiscriminatorValue("ORGANIZATION")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Organization extends AbstractUser {

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    private List<Bonus> bonuses;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Poll> polls;

    @ManyToMany
    @JoinTable(
            name = "organization_categories",
            joinColumns = @JoinColumn(name = "organization_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @ManyToMany(mappedBy = "likedOrganizations")
    private List<User> likedByUsers;

    private String description;

}

