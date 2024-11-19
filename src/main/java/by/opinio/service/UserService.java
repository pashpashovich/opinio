package by.opinio.service;

import by.opinio.domain.UserResponse;
import by.opinio.entity.AbstractUser;
import by.opinio.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

}
