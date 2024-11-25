package by.opinio.auth;


import by.opinio.Exception.AppException;
import by.opinio.config.JwtService;
import by.opinio.entity.Category;
import by.opinio.entity.Organization;
import by.opinio.entity.User;
import by.opinio.enums.Role;
import by.opinio.repository.CategoryRepository;
import by.opinio.repository.UserRepository;
import by.opinio.service.OrganizationService;
import by.opinio.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final OrganizationService organizationService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CategoryRepository categoryRepository;

    @Transactional
    public AuthenticationResponse register(RegisterRequestOrganization request) {
        if (!userService.isLoginAvailable(request.getLogin())) {
            throw new AppException("Login already exists: " + request.getLogin(), HttpStatus.CONFLICT);
        }

        Organization organization = Organization.builder()
                .username(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ORGANIZATION)
                .name(request.getName())
                .activityType(request.getActivityType())
                .mission(request.getMission())
                .description(request.getDescription())
                .email(request.getEmail())
                .createdAt(LocalDateTime.now())
                .phone(request.getPhone())
                .website(request.getWebsite())
                .build();

        userService.save(organization);

        List<Category> categories = new ArrayList<>(Arrays.stream(request.getTags())
                .map(tag -> categoryRepository.findByName(tag)
                        .orElseThrow(() -> new AppException("Category not found: " + tag, HttpStatus.NOT_FOUND)))
                .toList());

        organization.setCategories(categories);
        organizationService.save(organization);

        var jwtToken = jwtService.generateToken(organization);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }


    public AuthenticationResponse register(RegisterRequestUser request) {
        if (!userService.isLoginAvailable(request.getLogin())) {
            throw new AppException("Login already exists: " + request.getLogin(), HttpStatus.CONFLICT);
        }
        List<Category> categories = Arrays.stream(request.getTags())
                .map(tag -> categoryRepository.findByName(tag)
                        .orElseThrow(() -> new AppException("Category not found: " + tag, HttpStatus.NOT_FOUND)))
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
            throw new AppException( "Неверный логин или пароль", HttpStatus.UNAUTHORIZED);
        }
    }
}