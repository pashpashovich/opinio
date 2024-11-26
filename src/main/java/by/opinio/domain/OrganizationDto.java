package by.opinio.domain;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class OrganizationDto {
    private UUID id;
    private String name;
    private String description;
    private List<BonusDto> bonuses;
    private List<PollDto> polls;
    private List<CategoryDto> categories;
}