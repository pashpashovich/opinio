package by.opinio.repository;

import by.opinio.entity.Category;
import by.opinio.entity.Organization;
import by.opinio.interfeces.PopularOrganizationProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    List<Organization> findByCategoriesIn(Collection<Category> categories);
    List<Organization> findByCreatedAtAfter(LocalDateTime date);
    @Query("SELECT o.id AS id, o.name AS name, COUNT(u.id) AS subscriberCount " +
            "FROM Organization o " +
            "LEFT JOIN o.subscribers u " +
            "GROUP BY o.id, o.name " +
            "ORDER BY subscriberCount DESC")
    List<PopularOrganizationProjection> findTopOrganizationsWithMostSubscribers(Pageable pageable);


}
