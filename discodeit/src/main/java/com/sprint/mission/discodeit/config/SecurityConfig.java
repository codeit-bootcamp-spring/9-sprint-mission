package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.JWT.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.security.JWT.JwtLoginSuccessHandler;
import com.sprint.mission.discodeit.security.JWT.JwtLogoutHandler;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final LoginFailureHandler loginFailureHandler;
  private final JwtLoginSuccessHandler jwtLoginSuccessHandler;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtLogoutHandler jwtLogoutHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/api/auth/login",
                "/api/auth/logout",
                "/api/auth/csrf-token",
                "/api/auth/refresh",
                "/api/users",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/actuator/**",
                "/",
                "/index.html",
                "/assets/**",
                "/*.js",
                "/*.css",
                "/*.ico",
                "/ws/**"
            ).permitAll()
            .anyRequest().authenticated()  // 나머지는 인증 필요
        )
        .sessionManagement(management -> management
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            .successHandler(jwtLoginSuccessHandler)
            .failureHandler(loginFailureHandler)
        )
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .addLogoutHandler(jwtLogoutHandler)
            .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
        )
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint((request, response, authException) -> {
              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
              response.setContentType("application/json");
              response.setCharacterEncoding("UTF-8");
              response.getWriter().write("{\"status\":401,\"message\":\"인증이 필요합니다.\"}");
            })
            .accessDeniedHandler((request, response, accessDeniedException) -> {
              response.setStatus(HttpServletResponse.SC_FORBIDDEN);
              response.setContentType("application/json");
              response.setCharacterEncoding("UTF-8");
              response.getWriter().write("{\"status\":403,\"message\":\"권한이 없습니다.\"}");
            })
        );
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.withDefaultRolePrefix()
        .role("ADMIN").implies("CHANNEL_MANAGER")
        .role("CHANNEL_MANAGER").implies("USER")
        .build();
  }

  @Bean
  static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
      RoleHierarchy roleHierarchy) {
    DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    return handler;
  }
}