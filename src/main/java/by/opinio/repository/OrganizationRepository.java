package by.opinio.repository;

import by.opinio.entity.Category;
import by.opinio.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    List<Organization> findByCategoriesIn(Collection<List<Category>> categories);
}
