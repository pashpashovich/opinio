package by.opinio.controller;

import by.opinio.API.ApiResponse;
import by.opinio.auth.AuthenticationRequest;
import by.opinio.auth.AuthenticationResponse;
import by.opinio.auth.AuthenticationService;
import by.opinio.auth.RegisterRequestOrganization;
import by.opinio.auth.RegisterRequestUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService service;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);


    public AuthenticationController(AuthenticationService service) {
        this.service = service;
    }


    @PostMapping("sign-up-user")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> registerUser(@RequestBody RegisterRequestUser request) {
        AuthenticationResponse authenticationResponse = service.register(request);
        ApiResponse<AuthenticationResponse> apiResponse =  ApiResponse.<AuthenticationResponse>builder()
                .data(authenticationResponse)
                .status(true)
                .message("User registered successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/sign-up-org")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> registerOrg(@RequestBody RegisterRequestOrganization request) {
        AuthenticationResponse authenticationResponse = service.register(request);
        ApiResponse<AuthenticationResponse> apiResponse =  ApiResponse.<AuthenticationResponse>builder()
                .data(authenticationResponse)
                .status(true)
                .message("Organization registered successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }


    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse authenticationResponse = service.authenticate(request);
        ApiResponse<AuthenticationResponse> apiResponse =  ApiResponse.<AuthenticationResponse>builder()
                .data(authenticationResponse)
                .status(true)
                .message("Organization registered successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            SecurityContextHolder.clearContext();
            logger.info("User logged out: {}", authentication.getName());
        }
        return ResponseEntity.ok().build();
    }

}
