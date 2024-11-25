package by.opinio.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String question;

    @ManyToOne
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

}
