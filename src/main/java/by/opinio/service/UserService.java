package by.opinio.service;

import by.opinio.entity.AbstractUser;
import by.opinio.entity.User;
import by.opinio.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private UserRepository userRepository;


    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}
