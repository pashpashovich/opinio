package by.opinio.repository;


import by.opinio.entity.AbstractUser;
import by.opinio.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<AbstractUser, UUID> {
    AbstractUser findByUsername(String login);
    boolean existsAbstractUserByUsername(String login);

}

