package by.opinio.service;

import by.opinio.domain.CreatePostDto;
import by.opinio.domain.OrganizationPostDto;
import by.opinio.entity.Organization;
import by.opinio.entity.OrganizationPost;
import by.opinio.repository.OrganizationPostRepository;
import by.opinio.repository.OrganizationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class OrganizationPostService {

    private final OrganizationPostRepository organizationPostRepository;
    private final OrganizationRepository organizationRepository;

    public OrganizationPostService(OrganizationPostRepository organizationPostRepository,
                                   OrganizationRepository organizationRepository) {
        this.organizationPostRepository = organizationPostRepository;
        this.organizationRepository = organizationRepository;
    }

    public List<OrganizationPostDto> getPostsByOrganization(UUID organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));
        return organizationPostRepository.findByOrganizationId(organizationId).stream()
                .map(post -> new OrganizationPostDto(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getCreatedAt(),
                        organization.getName(),
                        post.getCommentCount()))
                .toList();
    }


    public OrganizationPostDto getPostById(UUID postId) {
        OrganizationPost post = organizationPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        return new OrganizationPostDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getOrganization().getName(),
                post.getCommentCount());
    }

    public OrganizationPostDto createPost(CreatePostDto createPostDto) {
        Organization organization = organizationRepository.findById(createPostDto.getOrganizationId())
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));

        OrganizationPost post = new OrganizationPost();
        post.setTitle(createPostDto.getTitle());
        post.setContent(createPostDto.getContent());
        post.setCreatedAt(LocalDateTime.now());
        post.setOrganization(organization);

        organizationPostRepository.save(post);

        return new OrganizationPostDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                organization.getName(),
                post.getCommentCount());
    }

    public void deletePost(UUID postId, UUID organizationId) throws AccessDeniedException {
        OrganizationPost post = organizationPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        if (!post.getOrganization().getId().equals(organizationId)) {
            throw new AccessDeniedException("Organization does not have permission to delete this post");
        }
        organizationPostRepository.delete(post);
    }

}
