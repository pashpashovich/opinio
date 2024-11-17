package by.opinio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "abstract_users")
@DiscriminatorValue("USER")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends AbstractUser {
    @Column(name = "activity_name")
    private String activityName; // Только для USER

    @Column(name = "birth_date")
    private LocalDate birthDate; // Только для USER

    @ManyToMany
    @JoinTable(
            name = "user_categories",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> interestedCategories;
}
