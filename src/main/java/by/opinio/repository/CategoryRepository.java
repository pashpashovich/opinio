package by.opinio.repository;

import by.opinio.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByNameIn(List<String> names);
    Optional<Category> findByName(String name);

    List<Category> findAllById(Iterable<UUID> ids);
}
