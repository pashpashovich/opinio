package by.opinio.controller;

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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;


@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService service;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);


    public AuthenticationController(AuthenticationService service) {
        this.service = service;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
                "error", ex.getReason()
        ));
    }


    @PostMapping("sign-up-user")
    public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody RegisterRequestUser request) {
        service.register(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign-up-org")
    public ResponseEntity<AuthenticationResponse> registerOrg(@RequestBody RegisterRequestOrganization request) {
        service.register(request);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
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
