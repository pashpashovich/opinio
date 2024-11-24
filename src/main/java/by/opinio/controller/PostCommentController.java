package by.opinio.controller;

import by.opinio.domain.CreateCommentDto;
import by.opinio.domain.PostCommentDto;
import by.opinio.service.PostCommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
public class PostCommentController {

    private final PostCommentService postCommentService;

    public PostCommentController(PostCommentService postCommentService) {
        this.postCommentService = postCommentService;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @GetMapping("/{postId}")
    public List<PostCommentDto> getCommentsByPost(@PathVariable UUID postId) {
        return postCommentService.getCommentsByPost(postId);
    }

    // Добавить комментарий к посту
    @PostMapping
    public ResponseEntity<PostCommentDto> addComment(@RequestBody CreateCommentDto createCommentDto) {
        return new ResponseEntity<>(postCommentService.addComment(createCommentDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable UUID commentId, @RequestParam UUID userId) throws AccessDeniedException {
        postCommentService.deleteComment(commentId, userId);
        return ResponseEntity.ok("Comment successfully deleted");
    }
}

