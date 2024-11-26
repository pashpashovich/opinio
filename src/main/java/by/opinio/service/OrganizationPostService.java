package by.opinio.service;

import by.opinio.Exception.AppException;
import by.opinio.domain.CreatePostDto;
import by.opinio.domain.OrganizationPostDto;
import by.opinio.entity.Organization;
import by.opinio.entity.OrganizationPost;
import by.opinio.repository.OrganizationPostRepository;
import by.opinio.repository.OrganizationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class OrganizationPostService {

    private final OrganizationPostRepository organizationPostRepository;
    private final OrganizationRepository organizationRepository;
    private final SubscriptionService subscriptionService;

    public OrganizationPostService(OrganizationPostRepository organizationPostRepository,
                                   OrganizationRepository organizationRepository, SubscriptionService subscriptionService) {
        this.organizationPostRepository = organizationPostRepository;
        this.organizationRepository = organizationRepository;
        this.subscriptionService = subscriptionService;
    }

    public List<OrganizationPostDto> getPostsByOrganization(UUID organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new AppException("Organization not found",HttpStatus.NOT_FOUND));
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
                .orElseThrow(() -> new AppException("Post not found", HttpStatus.NOT_FOUND));
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
                .orElseThrow(() -> new AppException("Organization not found", HttpStatus.NOT_FOUND));

        OrganizationPost post = new OrganizationPost();
        post.setTitle(createPostDto.getTitle());
        post.setContent(createPostDto.getContent());
        post.setCreatedAt(LocalDateTime.now());
        post.setOrganization(organization);

        organizationPostRepository.save(post);
        subscriptionService.notifySubscribers(organization, "New post created: " + post.getTitle());

        return new OrganizationPostDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                organization.getName(),
                post.getCommentCount());
    }

    public void deletePost(UUID postId, UUID organizationId)  {
        OrganizationPost post = organizationPostRepository.findById(postId)
                .orElseThrow(() -> new AppException("Post not found", HttpStatus.NOT_FOUND));
        if (!post.getOrganization().getId().equals(organizationId)) {
            throw new AppException("Organization does not have permission to delete this post", HttpStatus.CONFLICT);
        }
        organizationPostRepository.delete(post);
    }

}

