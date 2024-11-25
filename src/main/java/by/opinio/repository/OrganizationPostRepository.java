package by.opinio.repository;

import by.opinio.entity.OrganizationPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrganizationPostRepository extends JpaRepository<OrganizationPost,UUID> {
    List<OrganizationPost> findByOrganizationId(UUID id);
}
