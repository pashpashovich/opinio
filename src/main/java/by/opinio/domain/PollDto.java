package by.opinio.domain;

import by.opinio.entity.Bonus;
import by.opinio.entity.Category;
import by.opinio.entity.Organization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PollDto {
    private UUID id;
    private String title;
    private String description;
    private CategoryDto category;
    private OrganizationDto createdBy;
    private List<QuestionDto> questions;
    private List<BonusDto> bonuses; // Список бонусов

}

