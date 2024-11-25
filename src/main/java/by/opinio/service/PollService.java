package by.opinio.service;

import by.opinio.domain.BonusDto;
import by.opinio.domain.CategoryDto;
import by.opinio.domain.OrganizationDto;
import by.opinio.domain.PollDto;
import by.opinio.domain.QuestionDto;
import by.opinio.entity.Bonus;
import by.opinio.entity.Category;
import by.opinio.entity.Organization;
import by.opinio.entity.Poll;
import by.opinio.entity.Question;
import by.opinio.repository.BonusRepository;
import by.opinio.repository.CategoryRepository;
import by.opinio.repository.PollRepository;
import by.opinio.repository.QuestionRepository;
import by.opinio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository pollRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    private final BonusRepository bonusRepository;

    /**
     * Создание нового опроса.
     */
    public PollDto createPoll(PollDto pollDto) {
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
                .questions(new ArrayList<>()) // Инициализируем пустой список вопросов
                .bonuses(new ArrayList<>()) // Инициализируем пустой список бонусов
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        pollRepository.save(poll);
        return convertToDto(poll);
    }

    /**
     * Обновление существующего опроса.
     */
    public PollDto updatePoll(UUID pollId, PollDto pollDto) {
        // Получаем существующий опрос
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Poll not found"));

        // Обновляем основные поля опроса
        poll.setTitle(pollDto.getTitle());
        poll.setDescription(pollDto.getDescription());

        // Обновляем категорию, если указана
        if (pollDto.getCategory() != null && pollDto.getCategory().getId() != null) {
            Category category = categoryRepository.findById(pollDto.getCategory().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            poll.setCategory(category);
        }

        // Обновляем список вопросов
        if (pollDto.getQuestions() != null) {
            // Удаляем старые вопросы
            questionRepository.deleteAll(poll.getQuestions());

            // Добавляем новые вопросы
            List<Question> updatedQuestions = pollDto.getQuestions().stream()
                    .map(questionDto -> Question.builder()
                            .id(questionDto.getId() != null ? questionDto.getId() : null)
                            .question(questionDto.getQuestion())
                            .poll(poll)
                            .build())
                    .toList();
            questionRepository.saveAll(updatedQuestions);
            poll.setQuestions(updatedQuestions);
        }

        // Обновляем список бонусов
        if (pollDto.getBonuses() != null) {
            List<Bonus> updatedBonuses = pollDto.getBonuses().stream()
                    .map(bonusDto -> bonusRepository.findById(bonusDto.getId())
                            .orElseThrow(() -> new IllegalArgumentException("Bonus not found")))
                    .toList();
            poll.setBonuses(updatedBonuses);
        }

        // Обновляем время
        poll.setUpdatedAt(LocalDateTime.now());

        // Сохраняем изменения
        pollRepository.save(poll);
        return convertToDto(poll);
    }


    /**
     * Удаление опроса по ID.
     */
    public void deletePoll(UUID pollId) {
        if (!pollRepository.existsById(pollId)) {
            throw new IllegalArgumentException("Poll not found");
        }
        pollRepository.deleteById(pollId);
    }

    /**
     * Получение опроса по ID.
     */
    public PollDto getPollById(UUID pollId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Poll not found"));
        return convertToDto(poll);
    }

    /**
     * Получение всех опросов.
     */
    public List<PollDto> getAllPolls() {
        return pollRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Получение опросов по организациям.
     */
    public List<PollDto> getPollsByOrganization(UUID organizationId) {
        Organization organization = (Organization) userRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        return pollRepository.findByCreatedBy(organization).stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Получение опросов по категориям.
     */
    public List<PollDto> getPollsByCategories(List<UUID> categoryIds) {
        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.isEmpty()) {
            throw new IllegalArgumentException("No categories found for the given IDs");
        }

        return pollRepository.findByCategoryIn(categories).stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Получение опросов по категориям и организациям.
     */
    public List<PollDto> getPollsByOrganizationAndCategories(UUID organizationId, List<UUID> categoryIds) {
        Organization organization = (Organization) userRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.isEmpty()) {
            throw new IllegalArgumentException("No categories found for the given IDs");
        }

        return pollRepository.findByCreatedByAndCategoryIn(organization, categories).stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Добавление вопросов к опросу.
     */
    public void deleteQuestionFromPoll(UUID pollId, UUID questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        if (!question.getPoll().getId().equals(pollId)) {
            throw new IllegalArgumentException("Question does not belong to the given poll");
        }

        questionRepository.deleteById(questionId);
    }

    /**
     * Добавление вопросов к опросу.
     */
    public PollDto addQuestionsToPoll(UUID pollId, List<QuestionDto> questionDtos) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Poll not found"));

        List<Question> questions = questionDtos.stream()
                .map(dto -> Question.builder()
                        .question(dto.getQuestion())
                        .poll(poll)
                        .build())
                .toList();

        questionRepository.saveAll(questions);
        poll.setUpdatedAt(LocalDateTime.now());
        pollRepository.save(poll);

        return convertToDto(poll);
    }

    /**
     * Получение вопросов для опроса.
     */
    public List<QuestionDto> getQuestionsByPollId(UUID pollId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Poll not found"));

        return poll.getQuestions().stream()
                .map(question -> QuestionDto.builder()
                        .id(question.getId())
                        .question(question.getQuestion())
                        .build())
                .toList();
    }

    /**
     * Добавление бонусов к опросу.
     */
    public PollDto addBonusesToPoll(UUID pollId, List<UUID> bonusIds) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Poll not found"));

        List<Bonus> bonuses = bonusRepository.findAllById(bonusIds);
        poll.getBonuses().addAll(bonuses);
        poll.setUpdatedAt(LocalDateTime.now());

        pollRepository.save(poll);
        return convertToDto(poll);
    }
    /**
     * Удаление бонуса у опроса.
     */
    public PollDto removeBonusFromPoll(UUID pollId, UUID bonusId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Poll not found"));

        Bonus bonus = bonusRepository.findById(bonusId)
                .orElseThrow(() -> new IllegalArgumentException("Bonus not found"));

        if (!poll.getBonuses().remove(bonus)) {
            throw new IllegalArgumentException("Bonus not associated with the given poll");
        }

        pollRepository.save(poll);
        return convertToDto(poll);
    }

    /**
     * Преобразование Poll в DTO.
     */
    private PollDto convertToDto(Poll poll) {
        return PollDto.builder()
                .id(poll.getId())
                .title(poll.getTitle())
                .description(poll.getDescription())
                .category(poll.getCategory())
                .createdBy(poll.getCreatedBy())
                .questions(poll.getQuestions() != null ? poll.getQuestions().stream()
                        .map(question -> QuestionDto.builder()
                                .id(question.getId())
                                .question(question.getQuestion())
                                .build())
                        .toList() : new ArrayList<>())
                .bonuses(poll.getBonuses() != null ? poll.getBonuses().stream()
                        .map(bonus -> BonusDto.builder()
                                .id(bonus.getId())
                                .name(bonus.getName())
                                .description(bonus.getDescription())
                                .build())
                        .toList() : new ArrayList<>())
                .build();
    }


}





