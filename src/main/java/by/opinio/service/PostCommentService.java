package by.opinio.service;

import by.opinio.Exception.AppException;
import by.opinio.domain.CreateCommentDto;
import by.opinio.domain.PostCommentDto;
import by.opinio.entity.AbstractUser;
import by.opinio.entity.OrganizationPost;
import by.opinio.entity.PostComment;
import by.opinio.entity.User;
import by.opinio.repository.OrganizationPostRepository;
import by.opinio.repository.PostCommentRepository;
import by.opinio.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final OrganizationPostRepository organizationPostRepository;
    private final UserRepository userRepository;

    public PostCommentService(PostCommentRepository postCommentRepository,
                              OrganizationPostRepository organizationPostRepository,
                              UserRepository userRepository) {
        this.postCommentRepository = postCommentRepository;
        this.organizationPostRepository = organizationPostRepository;
        this.userRepository = userRepository;
    }

    public List<PostCommentDto> getCommentsByPostId(UUID postId) {
        return postCommentRepository.findByPostId(postId).stream()
                .map(comment -> new PostCommentDto(
                        comment.getId(),
                        comment.getContent(),
                        comment.getCreatedAt(),
                        comment.getUser().getUsername()))
                .collect(Collectors.toList());
    }

    public PostCommentDto addComment(CreateCommentDto createCommentDto) {
        OrganizationPost post = organizationPostRepository.findById(createCommentDto.getPostId())
                .orElseThrow(() -> new AppException("Post not found", HttpStatus.NOT_FOUND));

        AbstractUser user = userRepository.findById(createCommentDto.getUserId())
                .orElseThrow(() -> new AppException("User not found" , HttpStatus.NOT_FOUND));

        PostComment comment = new PostComment();
        comment.setContent(createCommentDto.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setPost(post);
        comment.setUser((User) user);
        postCommentRepository.save(comment);
        post.setCommentCount(post.getCommentCount() + 1);
        organizationPostRepository.save(post);
        return new PostCommentDto(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                user.getUsername());
    }

    public List<PostCommentDto> getCommentsByPost(UUID postId) {
        return postCommentRepository.findByPostId(postId).stream()
                .map(comment -> new PostCommentDto(
                        comment.getId(),
                        comment.getContent(),
                        comment.getCreatedAt(),
                        comment.getUser().getUsername()
                ))
                .collect(Collectors.toList());
    }


    public void deleteComment(UUID commentId, UUID userId) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new AppException("Comment not found", HttpStatus.NOT_FOUND));
        if (!comment.getUser().getId().equals(userId)) {
            throw new AppException("You do not have permission to delete this comment", HttpStatus.CONFLICT);
        }
        OrganizationPost post = comment.getPost();
        post.setCommentCount(post.getCommentCount() - 1);
        organizationPostRepository.save(post);
        postCommentRepository.delete(comment);
    }
}
