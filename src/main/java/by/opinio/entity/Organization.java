package by.opinio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "organizations")
@DiscriminatorValue("ORGANIZATION")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Organization extends AbstractUser {

    @Column(name = "name",unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "mission")
    private String mission;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "website")
    private String website;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    private List<Bonus> bonuses;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Poll> polls;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToMany
    @JoinTable(
            name = "organization_categories",
            joinColumns = @JoinColumn(name = "organization_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @ManyToMany(mappedBy = "likedOrganizations")
    private List<User> likedByUsers;

    @ManyToMany(mappedBy = "subscriptions")
    private List<User> subscribers;
}

