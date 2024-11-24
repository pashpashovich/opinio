package by.opinio.service;

import by.opinio.domain.PollDto;
import by.opinio.entity.Category;
import by.opinio.entity.Organization;
import by.opinio.entity.Poll;
import by.opinio.repository.CategoryRepository;
import by.opinio.repository.PollRepository;
import by.opinio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository pollRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Создание нового опроса.
     */
    public void createPoll(PollDto pollDto) {
        // Получаем организацию, которая создаёт опрос
        Organization createdBy = (Organization) userRepository.findById(pollDto.getCreatedBy().getId())
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        // Получаем категорию, к которой относится опрос
        Category category = categoryRepository.findById(pollDto.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        // Создаём новый опрос
        Poll poll = Poll.builder()
                .title(pollDto.getTitle())
                .description(pollDto.getDescription())
                .category(category)
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        pollRepository.save(poll);
    }

    /**
     * Обновление существующего опроса.
     */
    public void updatePoll(UUID pollId, PollDto pollDto) {
        // Получаем существующий опрос
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Poll not found"));

        // Обновляем данные опроса
        poll.setTitle(pollDto.getTitle());
        poll.setDescription(pollDto.getDescription());
        poll.setCategory(pollDto.getCategory());
        poll.setUpdatedAt(LocalDateTime.now());

        pollRepository.save(poll);
    }

    /**
     * Получение всех опросов.
     */
    public List<PollDto> getAllPolls() {
        return pollRepository.findAll().stream()
                .map(poll -> PollDto.builder()
                        .id(poll.getId())
                        .title(poll.getTitle())
                        .description(poll.getDescription())
                        .category(poll.getCategory())
                        .createdBy(poll.getCreatedBy())
                        .createdAt(poll.getCreatedAt())
                        .updatedAt(poll.getUpdatedAt())
                        .build())
                .toList();
    }

    /**
     * Получение всех опросов по организации.
     */
    public List<PollDto> getPollsByOrganization(UUID organizationId) {
        // Проверяем, что организация существует
        Organization organization = (Organization) userRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        // Получаем все опросы, созданные данной организацией
        return pollRepository.findByCreatedBy(organization).stream()
                .map(poll -> PollDto.builder()
                        .id(poll.getId())
                        .title(poll.getTitle())
                        .description(poll.getDescription())
                        .category(poll.getCategory())
                        .createdBy(poll.getCreatedBy())
                        .createdAt(poll.getCreatedAt())
                        .updatedAt(poll.getUpdatedAt())
                        .build())
                .toList();
    }
}




