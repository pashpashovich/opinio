package by.opinio.controller;

import by.opinio.API.ApiResponse;
import by.opinio.entity.User;
import by.opinio.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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


}
