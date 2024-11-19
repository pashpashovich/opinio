package by.opinio.repository;


import by.opinio.entity.AbstractUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AbstractUser,Long> {
    AbstractUser findByUsername(String login);

    boolean existsAbstractUserByUsername(String login);

}

