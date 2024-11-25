package by.opinio.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;

    private AuthenticationProvider authenticationProvider;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                .requestMatchers(HttpMethod.POST, "/api/auth/authenticate").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/sign-up-org").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/sign-up-user").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/mappings").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/posts").hasAuthority("ORGANIZATION")
                .requestMatchers(HttpMethod.DELETE, "/api/posts/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/posts/organization/*").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/posts/*").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/comments/*").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/comments").hasAuthority("USER")
                .requestMatchers(HttpMethod.DELETE, "/api/comments/*").hasAuthority("USER")
                .anyRequest().authenticated());
        return http.build();
    }

    @Bean
    public OncePerRequestFilter authenticationErrorFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
                try {
                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    try {
                        response.getWriter().write("{\"error\": \"Неверный логин или пароль\"}");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
    }


}