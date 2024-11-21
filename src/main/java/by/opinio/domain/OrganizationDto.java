package by.opinio.domain;

import by.opinio.entity.Bonus;
import by.opinio.entity.Poll;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrganizationDto {
    private UUID id;
    private String name;
    private String description;
    private List<BonusDto> bonuses;
    private List<PollDto> polls;
}