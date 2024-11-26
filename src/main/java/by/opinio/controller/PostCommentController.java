package by.opinio.controller;

import by.opinio.API.ApiResponse;
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
    public ResponseEntity<ApiResponse<List<PostCommentDto>>> getCommentsByPost(@PathVariable UUID postId) {
        List<PostCommentDto> comments = postCommentService.getCommentsByPost(postId);
        ApiResponse<List<PostCommentDto>> apiResponse = ApiResponse.<List<PostCommentDto>>builder()
                .data(comments)
                .status(true)
                .message("Comments fetched successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    // Добавить комментарий к посту
    @PostMapping
    public ResponseEntity<ApiResponse<PostCommentDto>> addComment(@RequestBody CreateCommentDto createCommentDto) {
        PostCommentDto createdComment = postCommentService.addComment(createCommentDto);
        ApiResponse<PostCommentDto> apiResponse = ApiResponse.<PostCommentDto>builder()
                .data(createdComment)
                .status(true)
                .message("Comment added successfully")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable UUID commentId, @RequestParam UUID userId) throws AccessDeniedException {
        postCommentService.deleteComment(commentId, userId);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .data(null)
                .status(true)
                .message("Comment deleted successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

}

