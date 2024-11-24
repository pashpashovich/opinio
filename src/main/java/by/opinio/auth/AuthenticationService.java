package by.opinio.auth;


import by.opinio.config.JwtService;
import by.opinio.entity.Category;
import by.opinio.entity.Organization;
import by.opinio.entity.User;
import by.opinio.enums.Role;
import by.opinio.repository.CategoryRepository;
import by.opinio.repository.UserRepository;
import by.opinio.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CategoryRepository categoryRepository;

    public AuthenticationResponse register(RegisterRequestOrganization request) {
        if (userService.isLoginAvailable(request.getLogin())) {
            List<Category> categories = Arrays.stream(request.getTags())
                    .map(tag -> categoryRepository.findByName(tag)
                            .orElseThrow(() -> new IllegalArgumentException("Category not found: " + tag)))
                    .toList();

            Organization organization = Organization.builder()
                    .username(request.getLogin())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.ORGANIZATION)
                    .activityType(request.getActivityType())
                    .categories(categories)
                    .build();
            userService.save(organization);
            var jwtToken = jwtService.generateToken(organization);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        }
        return null;
    }

    public AuthenticationResponse register(RegisterRequestUser request) {
        if (userService.isLoginAvailable(request.getLogin())) {
            List<Category> categories = Arrays.stream(request.getTags())
                    .map(tag -> categoryRepository.findByName(tag)
                            .orElseThrow(() -> new IllegalArgumentException("Category not found: " + tag)))
                    .toList();
            User user = User.builder()
                    .username(request.getLogin())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.USER)
                    .activityType(request.getActivityType())
                    .activityName(request.getActivityName())
                    .birthDate(request.getDateOfBirth())
                    .interestedCategories(categories)
                    .build();
            userService.save(user);
            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        }
        return null;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getLogin(),
                            request.getPassword()
                    )
            );
            var user = userRepository.findByUsername(request.getLogin());
            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .id(user.getId())
                    .role(user.getRole().name())
                    .build();
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный логин или пароль", ex);
        }
    }
}