package by.opinio.service;

import by.opinio.Exception.AppException;
import by.opinio.domain.UpdateUserDto;
import by.opinio.domain.UserDto;
import by.opinio.entity.AbstractUser;
import by.opinio.entity.Category;
import by.opinio.entity.Organization;
import by.opinio.entity.User;
import by.opinio.repository.CategoryRepository;
import by.opinio.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private  final CategoryRepository categoryRepository;

    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserService(UserRepository userRepository, CategoryRepository categoryRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean isLoginAvailable(String login) {
        return !userRepository.existsAbstractUserByUsername(login);

    }

    public void save(AbstractUser user) {
        userRepository.save(user);
    }

    public Optional<AbstractUser> findById(UUID userId) {
        return userRepository.findById(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(abstractUser -> abstractUser instanceof User user)
                .map(abstractUser -> (User)abstractUser)
                .toList();
    }

    public void uploadAvatar(UUID id, String base64Image) {
        AbstractUser user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setProfilePictureUrl(base64Image);
        userRepository.save(user);
    }

    public String getAvatar(UUID id) {
        return userRepository.findById(id)
                .map(AbstractUser::getProfilePictureUrl)
                .orElse(null);
    }
    public List<Category> updateInterestedCategories(UUID userId, List<UUID> categoryIds) {
        // Находим пользователя
        User user = (User) userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        // Получаем категории по ID
        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw new AppException("Some categories not found", HttpStatus.NOT_FOUND);
        }

        // Обновляем интересующие категории
        user.setInterestedCategories(categories);

        // Сохраняем изменения
        userRepository.save(user);

        return categories;
    }

    public UserDto getUserById(UUID userId) {
        User user = (User) userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        return convertToDto(user);
    }



    public void updateUser(UUID userId, UpdateUserDto updateUserDto) {
        // Получаем пользователя из базы данных
        User user = (User) userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        // Проверяем новый логин, если он не пустой
        if (updateUserDto.getUsername() != null && !updateUserDto.getUsername().isBlank()) {
            user.setUsername(updateUserDto.getUsername());
        }

        // Проверяем пароли
        if (updateUserDto.getPassword() != null && !updateUserDto.getPassword().isBlank()) {
            if (!updateUserDto.getPassword().equals(updateUserDto.getConfirmPassword())) {
                throw new AppException("Passwords do not match", HttpStatus.BAD_REQUEST);
            }
            user.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
        }

        // Обновляем род деятельности
        if (updateUserDto.getActivityType() != null) {
            user.setActivityType(updateUserDto.getActivityType());
        }

        // Обновляем дату рождения
        if (updateUserDto.getBirthDate() != null) {
            user.setBirthDate(updateUserDto.getBirthDate());
        }

        // Сохраняем изменения
        userRepository.save(user);

    }
    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .username(user.getUsername())
                .activityType(user.getActivityType())
                .activityName(user.getActivityName())
                .birthDate(user.getBirthDate())
                .interestedCategories(user.getInterestedCategories().stream()
                        .map(Category::getId)
                        .toList())
                .likedOrganizations(user.getLikedOrganizations().stream()
                        .map(Organization::getId)
                        .toList())
                .subscriptions(user.getSubscriptions().stream()
                        .map(Organization::getId)
                        .toList())
                .build();
    }

}
