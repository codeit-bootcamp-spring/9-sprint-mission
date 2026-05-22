package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.BearerTokenAuthenticationFilter;
import com.sprint.mission.discodeit.security.JwtLoginSuccessHandler;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      JwtLoginSuccessHandler jwtLoginSuccessHandler,
      LoginFailureHandler loginFailureHandler,
      ObjectMapper objectMapper,
      BearerTokenAuthenticationFilter bearerTokenAuthenticationFilter
  ) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.GET, "/api/auth/csrf-token").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
            .requestMatchers(
                "/",
                "/index.html",
                "/assets/**",
                "/favicon.ico",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/actuator/**"
            ).permitAll()
            .requestMatchers("/api/**").authenticated()
            .anyRequest().permitAll()
        )
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint((request, response, authException) ->
                writeErrorResponse(response, objectMapper, ErrorCode.AUTHENTICATION_REQUIRED,
                    authException)
            )
            .accessDeniedHandler((request, response, accessDeniedException) ->
                writeErrorResponse(response, objectMapper, ErrorCode.ACCESS_DENIED,
                    accessDeniedException)
            )
        )
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            .successHandler(jwtLoginSuccessHandler)
            .failureHandler(loginFailureHandler)
        )
        .sessionManagement(management -> management
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
            // 기본값으로 설정이 되있지만 인증 의도 보여주기 위해 작성
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID", JwtLoginSuccessHandler.REFRESH_TOKEN_COOKIE_NAME)
        )
        .addFilterBefore(bearerTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  @Bean
  public BearerTokenAuthenticationFilter bearerTokenAuthenticationFilter(
      JwtTokenProvider jwtTokenProvider,
      UserDetailsService userDetailsService
  ) {
    return new BearerTokenAuthenticationFilter(jwtTokenProvider, userDetailsService);
  }

  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.withDefaultRolePrefix()
        .role("ADMIN").implies("CHANNEL_MANAGER", "USER")
        .role("CHANNEL_MANAGER").implies("USER")
        .build();
  }

  @Bean
  static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
      RoleHierarchy roleHierarchy
  ) {
    DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    return handler;
  }

  private void writeErrorResponse(
      HttpServletResponse response,
      ObjectMapper objectMapper,
      ErrorCode errorCode,
      Exception exception
  ) throws IOException {
    ErrorResponse body = new ErrorResponse(
        Instant.now(),
        errorCode.getCode(),
        errorCode.getMessage(),
        Map.of(),
        exception.getClass().getSimpleName(),
        errorCode.getStatus().value()
    );

    response.setStatus(errorCode.getStatus().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    objectMapper.writeValue(response.getWriter(), body);
  }

}
