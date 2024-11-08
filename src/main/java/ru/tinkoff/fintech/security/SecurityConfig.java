package ru.tinkoff.fintech.security;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import ru.tinkoff.fintech.user.repository.UserRepository;
import ru.tinkoff.fintech.utils.JwtTokenUtils;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> {
                    requests
                            .requestMatchers(HttpMethod.PUT, "/api/events/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/api/events/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.POST, "/api/events**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/api/places/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/api/places/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.POST, "/api/places**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.POST, "/api/user/password").authenticated()
                            .requestMatchers(HttpMethod.POST, "/api/user/logout").authenticated()
                            .anyRequest().permitAll();
                })
                .exceptionHandling(exception ->
                        exception
                                .accessDeniedHandler(accessDeniedHandler())
                                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .sessionManagement(configurer -> {
                    configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .addFilterBefore(new JwtFilter(userRepository, jwtTokenUtils),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsFilter corsFilter() {
        var source = new UrlBasedCorsConfigurationSource();
        var config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return (httpServletRequest, httpServletResponse, e) -> {
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            httpServletResponse
                    .getWriter()
                    .write("{\"status\": 403, \"message\": \"" +
                            "У вас нет прав доступа" + "\", \"timestamp\": \"" + Instant.now() + "\"}");
        };
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
