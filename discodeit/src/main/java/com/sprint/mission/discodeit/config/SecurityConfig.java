package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.handler.JwtLoginSuccessHandler;
import com.sprint.mission.discodeit.handler.JwtLogoutHandler;
import com.sprint.mission.discodeit.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.handler.LoginSuccessHandler;
import com.sprint.mission.discodeit.handler.SpaCsrfTokenRequestHandler;
import com.sprint.mission.discodeit.service.basic.DiscodeitUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final ObjectMapper objectMapper;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http,
      JwtLoginSuccessHandler jwtLoginSuccessHandler, JwtLogoutHandler jwtLogoutHandler,
      LoginFailureHandler loginFailureHandler, DiscodeitUserDetailsService userDetailsService,
      JwtAuthenticationFilter jwtAuthenticationFilter)
      throws Exception {
    http
        .authorizeHttpRequests(authz -> authz
            // 프론트 코드 및 기본 페이지 허용
            .requestMatchers("/", "/index.html", "/assets/**").permitAll()
            .requestMatchers("/api/auth/role").hasRole("ADMIN")
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/ws/**").permitAll()
            // 회원 가입 요청 허용
            .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
            // Swagger
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            // Actuator
            .requestMatchers("/actuator/**").permitAll()
            // 다운
            .requestMatchers("/api/binaryContents/**").permitAll()
            .requestMatchers("/error").permitAll()
            .anyRequest().authenticated()
        )
        .csrf(
            csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
        )
        //.formLogin(Customizer.withDefaults());
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            .successHandler(jwtLoginSuccessHandler)
            .failureHandler(loginFailureHandler)
        )
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .addLogoutHandler(jwtLogoutHandler)
            .logoutSuccessHandler(
                new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)))
        .exceptionHandling(ex -> ex
            // 로그인 안 한 경우
            .authenticationEntryPoint((request, response, authException) -> {
              ErrorResponse errorResponse = new ErrorResponse(
                  Instant.now(),
                  "UNAUTHORIZED",
                  "인증이 필요합니다.",
                  Map.of("reason", authException.getMessage()),
                  authException.getClass().getSimpleName(),
                  HttpStatus.UNAUTHORIZED.value()
              );
              response.setStatus(HttpStatus.UNAUTHORIZED.value());
              response.setContentType("application/json;charset=UTF-8");
              response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            })
            .accessDeniedHandler((request, response, accessDeniedException) -> {
              ErrorResponse errorResponse = new ErrorResponse(
                  Instant.now(),
                  "FORBIDDEN",
                  "접근 권한이 없습니다.",
                  Map.of("reason", accessDeniedException.getMessage()),
                  accessDeniedException.getClass().getSimpleName(),
                  HttpStatus.FORBIDDEN.value()
              );
              response.setStatus(HttpStatus.FORBIDDEN.value());
              response.setContentType("application/json;charset=UTF-8");
              response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            })
        )
        .sessionManagement(management -> management
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
    ;

    return http.build();
  }
}