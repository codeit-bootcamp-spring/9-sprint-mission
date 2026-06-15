package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.auth.CsrfCookieFilter;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.handler.*;
import com.sprint.mission.discodeit.handler.SpaCsrfTokenRequestHandler;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor

public class SecurityConfig {

    private final JwtLoginSuccessHandler jwtLoginSuccessHandler;
    private final JwtLogoutHandler jwtLogoutHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String[] STATIC_RESOURCES = {
            "/", "/index.html", "/static/**", "/assets/**", "*.js", "*.css", "*.ico"
    };

    private static final String[] SWAGGER_RESOURCES = {
            "/swagger-ui/**", "/v3/api-docs/**", "/actuator/**"
    };

    private static final String[] AUTH_API_RESOURCES = {
            "/api/auth/csrf-token", "/api/auth/login", "/api/auth/logout", "/api/auth/**", "/api/auth/refresh"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                )
                .addFilterAfter(new CsrfCookieFilter(), CsrfFilter.class)
                .formLogin(login -> login.loginProcessingUrl("/api/auth/login")
                        .successHandler(jwtLoginSuccessHandler)
                        .failureHandler(loginFailureHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/me").authenticated()
                        .requestMatchers(STATIC_RESOURCES).permitAll()
                        .requestMatchers(AUTH_API_RESOURCES).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users").permitAll()
                        .requestMatchers(SWAGGER_RESOURCES).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        ))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout.logoutUrl("/api/auth/logout")
                        .addLogoutHandler(jwtLogoutHandler)
                        .logoutSuccessHandler(
                                new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}