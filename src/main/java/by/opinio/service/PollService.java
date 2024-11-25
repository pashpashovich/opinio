package by.opinio.service;

import by.opinio.Exception.AppException;
import by.opinio.domain.*;
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
import org.springframework.http.HttpStatus;
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

        // Проверяем существование организации
        Organization createdBy = (Organization) userRepository.findById(pollDto.getCreatedBy().getId())
                .orElseThrow(() -> new AppException("Organization not found", HttpStatus.NOT_FOUND));

        // Проверяем существование категории
        Category category = categoryRepository.findById(pollDto.getCategory().getId())
                .orElseThrow(() -> new AppException("Category not found", HttpStatus.NOT_FOUND));

        // Создаём новый опрос
        Poll poll = Poll.builder()
                .title(pollDto.getTitle())
                .description(pollDto.getDescription())
                .category(category)
                .createdBy(createdBy)
                .questions(new ArrayList<>()) // Пустой список вопросов
                .bonuses(new ArrayList<>())   // Пустой список бонусов
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        pollRepository.save(poll);

        // Добавляем вопросы
        if (pollDto.getQuestions() != null && !pollDto.getQuestions().isEmpty()) {
            List<Question> questions = new ArrayList<>(); // Изменяемый список
            pollDto.getQuestions().stream()
                    .filter(questionDto -> questionDto.getQuestion() != null) // Фильтрация
                    .forEach(questionDto -> {
                        Question question = Question.builder()
                                .question(questionDto.getQuestion())
                                .poll(poll)
                                .build();
                        questions.add(question);
                    });
            questionRepository.saveAll(questions);
            poll.setQuestions(questions);
        }

        // Добавляем бонусы
        if (pollDto.getBonuses() != null && !pollDto.getBonuses().isEmpty()) {
            List<Bonus> bonuses = new ArrayList<>(); // Изменяемый список
            pollDto.getBonuses().stream()
                    .filter(bonusDto -> bonusDto.getId() != null) // Фильтрация
                    .forEach(bonusDto -> {
                        Bonus bonus = bonusRepository.findById(bonusDto.getId())
                                .orElseThrow(() -> new AppException("Bonus not found", HttpStatus.NOT_FOUND));
                        bonuses.add(bonus);
                    });
            poll.setBonuses(bonuses);
        }

        // Сохраняем опрос с обновлёнными данными
        poll.setUpdatedAt(LocalDateTime.now());
        pollRepository.save(poll);

        return convertToDto(poll);
    }


    /**
     * Обновление существующего опроса.
     */
    public PollDto updatePoll(UUID pollId, PollDto pollDto) {
        // Получаем существующий опрос
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new AppException("Poll not found", HttpStatus.NOT_FOUND));

        // Обновляем основные поля опроса
        poll.setTitle(pollDto.getTitle());
        poll.setDescription(pollDto.getDescription());

        // Обновляем категорию, если указана
        if (pollDto.getCategory() != null && pollDto.getCategory().getId() != null) {
            Category category = categoryRepository.findById(pollDto.getCategory().getId())
                    .orElseThrow(() -> new AppException("Category not found", HttpStatus.NOT_FOUND));
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
                            .orElseThrow(() -> new AppException("Bonus not found", HttpStatus.NOT_FOUND)))
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
            throw new AppException("Poll not found", HttpStatus.NOT_FOUND);
        }
        pollRepository.deleteById(pollId);
    }

    /**
     * Получение опроса по ID.
     */
    public PollDto getPollById(UUID pollId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new AppException("Poll not found", HttpStatus.NOT_FOUND));
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
                .orElseThrow(() -> new AppException("Organization not found", HttpStatus.NOT_FOUND));

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
            throw new AppException("No categories found for the given IDs", HttpStatus.NO_CONTENT);
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
                .orElseThrow(() -> new AppException("Organization not found", HttpStatus.NOT_FOUND));

        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.isEmpty()) {
            throw new AppException("No categories found for the given IDs", HttpStatus.NO_CONTENT);
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
                .orElseThrow(() -> new AppException("Question not found", HttpStatus.NOT_FOUND));

        if (!question.getPoll().getId().equals(pollId)) {
            throw new AppException("Question does not belong to the given poll", HttpStatus.NOT_FOUND);
        }

        questionRepository.deleteById(questionId);
    }

    /**
     * Добавление вопросов к опросу.
     */
    public PollDto addQuestionsToPoll(UUID pollId, List<QuestionDto> questionDtos) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new AppException("Poll not found", HttpStatus.NOT_FOUND));

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
                .orElseThrow(() -> new AppException("Poll not found", HttpStatus.NOT_FOUND));

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
                .orElseThrow(() -> new AppException("Poll not found", HttpStatus.NOT_FOUND));

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
                .orElseThrow(() -> new AppException("Poll not found", HttpStatus.NOT_FOUND));

        Bonus bonus = bonusRepository.findById(bonusId)
                .orElseThrow(() -> new AppException("Bonus not found", HttpStatus.NOT_FOUND));

        if (!poll.getBonuses().remove(bonus)) {
            throw new AppException("Bonus not associated with the given poll", HttpStatus.NOT_FOUND);
        }

        pollRepository.save(poll);
        return convertToDto(poll);
    }

    /**
     * Получение новинок опросов.
     */
    public List<PollDto> getNewPolls() {
        // Определяем дату 7 дней назад
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        // Находим опросы, созданные после указанной даты
        List<Poll> newPolls = pollRepository.findByCreatedAtAfter(sevenDaysAgo);

        // Конвертируем в DTO
        return newPolls.stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Преобразование Poll в DTO.
     */
    private PollDto convertToDto(Poll poll) {
        return PollDto.builder()
                .id(poll.getId())
                .title(poll.getTitle())
                .description(poll.getDescription())
                .category(categoryDto(poll.getCategory()))
                .createdBy(convert(poll.getCreatedBy()) )
                .questions(convert(poll.getQuestions()) )
                .build();
    }

    private CategoryDto categoryDto(Category categoryDto){
        return CategoryDto.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }
    private OrganizationDto convert(Organization organization){
        return OrganizationDto.builder()
                .id(organization.getId())
                .name(organization.getName())
                .build();
    }
    private List<QuestionDto> convert(List<Question> question){
         List<QuestionDto> questionDtos = new ArrayList<>();
         for (Question questionDto : question) {
            questionDtos.add(QuestionDto.builder()
                            .id(questionDto.getId())
                            .question(questionDto.getQuestion())
                    .build());
         }
         return questionDtos;
    }
}




