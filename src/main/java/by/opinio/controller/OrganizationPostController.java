package by.opinio.controller;

import by.opinio.domain.CreatePostDto;
import by.opinio.domain.OrganizationPostDto;
import by.opinio.service.OrganizationPostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
    public List<OrganizationPostDto> getPostsByOrganization(@PathVariable UUID organizationId) {
        return organizationPostService.getPostsByOrganization(organizationId);
    }

    @GetMapping("/{postId}")
    public OrganizationPostDto getPostById(@PathVariable UUID postId) {
        return organizationPostService.getPostById(postId);
    }

    @PostMapping
    public ResponseEntity<OrganizationPostDto> createPost(@RequestBody CreatePostDto createPostDto) {
        return new ResponseEntity<>(organizationPostService.createPost(createPostDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable UUID postId, @RequestParam UUID organizationId) throws AccessDeniedException {
        organizationPostService.deletePost(postId, organizationId);
        return ResponseEntity.ok("Post successfully deleted");
    }

}
