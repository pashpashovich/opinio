package by.opinio.service;

import by.opinio.domain.BonusDto;
import by.opinio.domain.OrganizationDto;
import by.opinio.domain.PollDto;
import by.opinio.entity.AbstractUser;
import by.opinio.entity.Organization;
import by.opinio.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final UserRepository userRepository;

    public OrganizationDto getOrganizationInfo(UUID organizationId) {
        AbstractUser user = userRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));

        if (!(user instanceof Organization)) {
            throw new IllegalArgumentException("The provided ID does not belong to an organization");
        }

        Organization organization = (Organization) user;

        return OrganizationDto.builder()
                .id(organization.getId())
                .name(organization.getName())
                .description(organization.getDescription())
                .bonuses(organization.getBonuses().stream()
                        .map(bonus -> BonusDto.builder()
                                .id(bonus.getId())
                                .name(bonus.getName())
                                .description(bonus.getDescription())
                                .createdAt(bonus.getCreatedAt())
                                .updatedAt(bonus.getUpdatedAt())
                                .build())
                        .toList())
                .polls(organization.getPolls().stream()
                        .map(poll -> PollDto.builder()
                                .id(poll.getId())
                                .title(poll.getTitle())
                                .description(poll.getDescription())
                                .createdAt(poll.getCreatedAt())
                                .updatedAt(poll.getUpdatedAt())
                                .build())
                        .toList())
                .build();
    }

}
