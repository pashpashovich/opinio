package by.opinio.service;

import by.opinio.Exception.AppException;
import by.opinio.domain.BonusDto;
import by.opinio.domain.CategoryDto;
import by.opinio.domain.OrganizationDto;
import by.opinio.domain.PollDto;
import by.opinio.domain.PopularOrganizationDto;
import by.opinio.entity.*;
import by.opinio.interfeces.PopularOrganizationProjection;
import by.opinio.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final CategoryRepository categoryRepository;
    private final BonusRepository bonusRepository;

    public void save(Organization organization) {
        try {
            organizationRepository.save(organization);
        } catch (DataIntegrityViolationException e) {
            throw new AppException("Failed to save organization: Integrity constraint violation", HttpStatus.CONFLICT);
        } catch (JpaSystemException e) {
            throw new AppException("Failed to save organization: JPA system error",HttpStatus.CONFLICT);
        } catch (Exception e) {
            throw new AppException("Unexpected error while saving organization", HttpStatus.CONFLICT);
        }
    }


    public OrganizationDto getOrganizationInfo(UUID organizationId) {
        AbstractUser user = userRepository.findById(organizationId)
                .orElseThrow(() -> new AppException("Organization not found", HttpStatus.NOT_FOUND));

        if (!(user instanceof Organization organization)) {
            throw new AppException("The provided ID does not belong to an organization", HttpStatus.CONFLICT);
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
                                .build())
                        .toList())
                .polls(organization.getPolls().stream()
                        .map(poll -> PollDto.builder()
                                .id(poll.getId())
                                .title(poll.getTitle())
                                .description(poll.getDescription())
                                .createdBy(convert(poll.getCreatedBy()))
                                .build())
                        .toList())
                .categories(organization.getCategories().stream()
                        .map(category -> CategoryDto.builder()
                                .id(category.getId())
                                .name(category.getName())
                                .build())
                        .toList())
                .build();
    }

    public List<OrganizationDto> getOrganizationsByUserCategories(UUID userId) {

        AbstractUser user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));


        if (!(user instanceof User actualUser)) {
            throw new AppException("Provided ID does not belong to a user", HttpStatus.NOT_FOUND);
        }


        List<Category> interestedCategories = actualUser.getInterestedCategories();
        if (interestedCategories == null || interestedCategories.isEmpty()) {
            throw new AppException("User has no interested categories", HttpStatus.CONFLICT);
        }

        return organizationRepository.findByCategoriesIn(interestedCategories).stream()

                .map(this::convertToDto)
                .toList();
    }


    public List<OrganizationDto> getLikedOrganizations(UUID userId) {
        AbstractUser user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        if (!(user instanceof User actualUser)) {
            throw new AppException("Provided ID does not belong to a valid user", HttpStatus.CONFLICT);
        }

        return actualUser.getLikedOrganizations().stream()
                .map(this::convertToDto)
                .toList();
    }

    public void addLikedOrganization(UUID userId, UUID organizationId) {
        AbstractUser user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new AppException("Organization not found", HttpStatus.NOT_FOUND));

        User actualUser = extractUser(user);
        if (!actualUser.getLikedOrganizations().contains(organization)) {
            actualUser.getLikedOrganizations().add(organization);
            userRepository.save(actualUser);
        }
    }

    public void removeLikedOrganization(UUID userId, UUID organizationId) {
        AbstractUser user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new AppException("Organization not found", HttpStatus.NOT_FOUND));

        User actualUser = extractUser(user);
        if (actualUser.getLikedOrganizations().contains(organization)) {
            actualUser.getLikedOrganizations().remove(organization);
            userRepository.save(actualUser);
        } else {
            throw new AppException("Данная организация не была лайкнутой...", HttpStatus.CONFLICT);
        }
    }

    public List<OrganizationDto> getOrganizationsByUserInterests(UUID userId) {

        AbstractUser user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        if (!(user instanceof User actualUser)) {
            throw new AppException("Provided ID does not belong to a valid user", HttpStatus.NOT_FOUND);
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
    /**
     * Получение новых организаций.
     */
    public List<OrganizationDto> getNewOrganizations() {
        // Определяем дату 7 дней назад
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        // Находим организации, созданные после указанной даты
        List<Organization> newOrganizations = organizationRepository.findByCreatedAtAfter(sevenDaysAgo);

        // Конвертируем в DTO
        return newOrganizations.stream()
                .map(this::convertToDto)
                .toList();
    }

    public List<BonusDto> getOrganizationsBonuses(UUID organizationId){
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new AppException("Organization not found", HttpStatus.NOT_FOUND));
        List<Bonus> bonuses = bonusRepository.findAllByOrganization(organization);
        return bonuses.stream()
                .map(this::convertToBonusesDto)
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

    /**
     * Получение 10 самых популярных организаций.
     */
    public List<PopularOrganizationDto> getTopOrganizations() {
        Pageable pageable = PageRequest.of(0, 10); // Первая страница, 10 записей
        List<PopularOrganizationProjection> projections = organizationRepository.findTopOrganizationsWithMostSubscribers(pageable);

        // Конвертация в DTO
        return projections.stream()
                .map(projection -> PopularOrganizationDto.builder()
                        .id(projection.getId())
                        .name(projection.getName())
                        .subscriberCount(projection.getSubscriberCount())
                        .build())
                .toList();
    }

    private CategoryDto convertCategoryToDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
    private BonusDto convertToBonusesDto(Bonus bonus) {
        return BonusDto.builder()
                .id(bonus.getId())
                .name(bonus.getName())
                .description(bonus.getDescription())
                .build();
    }

    private User extractUser(AbstractUser user) {
        if (user instanceof User actualUser) {
            return actualUser;
        }
        throw new AppException("Provided ID does not belong to a valid user", HttpStatus.CONFLICT);
    }
    private OrganizationDto convert(Organization organization){
        return OrganizationDto.builder()
                .id(organization.getId())
                .name(organization.getName())
                .build();
    }


}
