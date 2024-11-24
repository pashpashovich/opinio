package by.opinio.service;

import by.opinio.domain.BonusDto;
import by.opinio.domain.CategoryDto;
import by.opinio.domain.OrganizationDto;
import by.opinio.domain.PollDto;
import by.opinio.entity.AbstractUser;
import by.opinio.entity.Category;
import by.opinio.entity.Organization;
import by.opinio.entity.User;
import by.opinio.repository.CategoryRepository;
import by.opinio.repository.OrganizationRepository;
import by.opinio.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final CategoryRepository categoryRepository;

    public void save(Organization organization) {
        try {
            organizationRepository.save(organization);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Failed to save organization: Integrity constraint violation", e);
        } catch (JpaSystemException e) {
            throw new IllegalStateException("Failed to save organization: JPA system error", e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while saving organization", e);
        }
    }


    public OrganizationDto getOrganizationInfo(UUID organizationId) {
        AbstractUser user = userRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));

        if (!(user instanceof Organization organization)) {
            throw new IllegalArgumentException("The provided ID does not belong to an organization");
        }

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
                                .createdBy(organization)
                                .build())
                        .toList())
                .build();
    }

    public List<OrganizationDto> getOrganizationsByUserCategories(UUID userId) {

        AbstractUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));


        if (!(user instanceof User actualUser)) {
            throw new IllegalArgumentException("Provided ID does not belong to a user");
        }


        List<Category> interestedCategories = actualUser.getInterestedCategories();
        if (interestedCategories == null || interestedCategories.isEmpty()) {
            throw new IllegalStateException("User has no interested categories");
        }


        return organizationRepository.findByCategoriesIn(interestedCategories).stream()
                .map(this::convertToDto)
                .toList();
    }


    public List<OrganizationDto> getLikedOrganizations(UUID userId) {
        AbstractUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!(user instanceof User actualUser)) {
            throw new IllegalArgumentException("Provided ID does not belong to a valid user");
        }

        return actualUser.getLikedOrganizations().stream()
                .map(this::convertToDto)
                .toList();
    }

    public void addLikedOrganization(UUID userId, UUID organizationId) {
        AbstractUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        User actualUser = extractUser(user);
        if (!actualUser.getLikedOrganizations().contains(organization)) {
            actualUser.getLikedOrganizations().add(organization);
            userRepository.save(actualUser);
        }
    }

    public void removeLikedOrganization(UUID userId, UUID organizationId) {
        AbstractUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        User actualUser = extractUser(user);
        if (actualUser.getLikedOrganizations().contains(organization)) {
            actualUser.getLikedOrganizations().remove(organization);
            userRepository.save(actualUser);
        } else {
            throw new IllegalArgumentException("Данная организация не была лайкнутой...");
        }
    }

    public List<OrganizationDto> getOrganizationsByUserInterests(UUID userId) {

        AbstractUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!(user instanceof User actualUser)) {
            throw new IllegalArgumentException("Provided ID does not belong to a valid user");
        }

        if (actualUser.getInterestedCategories() == null || actualUser.getInterestedCategories().isEmpty()) {
            return List.of();
        }

        List<Category> interestedCategories = actualUser.getInterestedCategories();
        return organizationRepository.findByCategoriesIn(interestedCategories).stream()
                .map(this::convertToDto)
                .toList();
    }

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertCategoryToDto)
                .toList();
    }

    private OrganizationDto convertToDto(Organization organization) {
        return OrganizationDto.builder()
                .id(organization.getId())
                .name(organization.getName())
                .description(organization.getDescription())
                .categories(organization.getCategories().stream()
                        .map(this::convertCategoryToDto)
                        .toList())
                .build();
    }

    private CategoryDto convertCategoryToDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    private User extractUser(AbstractUser user) {
        if (user instanceof User actualUser) {
            return actualUser;
        }
        throw new IllegalArgumentException("Provided ID does not belong to a valid user");
    }
}
