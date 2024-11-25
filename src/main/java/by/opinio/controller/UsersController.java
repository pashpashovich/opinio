package by.opinio.controller;

import by.opinio.API.ApiResponse;
import by.opinio.entity.User;
import by.opinio.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {
    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        ApiResponse<List<User>> apiResponse = ApiResponse.<List<User>>builder()
                .data(users)
                .status(true)
                .message("Users fetched successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("avatar/{id}")
    public ResponseEntity<Void> uploadAvatar(@PathVariable UUID id, @RequestBody String base64Image) {
        userService.uploadAvatar(id, base64Image);
        return ResponseEntity.ok().build();
    }

    @GetMapping("avatar/{id}")
    public ResponseEntity<String> getAvatar(@PathVariable UUID id) {
        String avatar = userService.getAvatar(id);
        return ResponseEntity.ok(avatar);
    }


}
