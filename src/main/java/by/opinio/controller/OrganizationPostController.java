package by.opinio.controller;

import by.opinio.API.ApiResponse;
import by.opinio.domain.CreatePostDto;
import by.opinio.domain.OrganizationPostDto;
import by.opinio.service.OrganizationPostService;
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
@RequestMapping("/api/posts")
public class OrganizationPostController {

    private final OrganizationPostService organizationPostService;

    public OrganizationPostController(OrganizationPostService organizationPostService) {
        this.organizationPostService = organizationPostService;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<ApiResponse<List<OrganizationPostDto>>> getPostsByOrganization(@PathVariable UUID organizationId) {
        List<OrganizationPostDto> organizationPosts = organizationPostService.getPostsByOrganization(organizationId);
        ApiResponse<List<OrganizationPostDto>> apiResponse = ApiResponse.<List<OrganizationPostDto>>builder()
                .data(organizationPosts)
                .status(true)
                .message("Organization posts returned successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<OrganizationPostDto>> getPostById(@PathVariable UUID postId) {
        OrganizationPostDto organizationPostDtos = organizationPostService.getPostById(postId);
        ApiResponse<OrganizationPostDto> apiResponse = ApiResponse.<OrganizationPostDto>builder()
                .data(organizationPostDtos)
                .status(true)
                .message("Post by id taken successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrganizationPostDto>> createPost(@RequestBody CreatePostDto createPostDto) {
        OrganizationPostDto organizationPostDto = organizationPostService.createPost(createPostDto);
        ApiResponse<OrganizationPostDto> apiResponse = ApiResponse.<OrganizationPostDto>builder()
                .data(organizationPostDto)
                .status(true)
                .message("Post created successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable UUID postId, @RequestParam UUID organizationId) throws AccessDeniedException {
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .status(true)
                .message("Post successfully deleted")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

}
