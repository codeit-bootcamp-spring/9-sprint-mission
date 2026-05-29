package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.Http403ForbiddenAccessDeniedHandler;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;
import com.sprint.mission.discodeit.security.jwt.InMemoryJwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.security.jwt.JwtLoginSuccessHandler;
import com.sprint.mission.discodeit.security.jwt.JwtLogoutHandler;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import java.util.List;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;

@Slf4j
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
      JwtAuthenticationFilter jwtAuthenticationFilter,
      JwtLogoutHandler jwtLogoutHandler
  )
      throws Exception {
    http
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
        )
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            .successHandler(jwtLoginSuccessHandler)
            .failureHandler(loginFailureHandler)
        )
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .addLogoutHandler(jwtLogoutHandler)
            .logoutSuccessHandler(
                new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/auth/csrf-token"),
                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/users"),
                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/auth/login"),
                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/auth/refresh"),
                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/auth/logout"),
                new NegatedRequestMatcher(AntPathRequestMatcher.antMatcher("/api/**"))
            ).permitAll()
            .anyRequest().authenticated()
        )
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
            .accessDeniedHandler(new Http403ForbiddenAccessDeniedHandler(objectMapper))
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        // Add JWT authentication filter
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
    ;
    return http.build();
  }

  @Bean
  public CommandLineRunner debugFilterChain(SecurityFilterChain filterChain) {
    return args -> {
      int filterSize = filterChain.getFilters().size();
      List<String> filterNames = IntStream.range(0, filterSize)
          .mapToObj(idx -> String.format("\t[%s/%s] %s", idx + 1, filterSize,
              filterChain.getFilters().get(idx).getClass()))
          .toList();
      log.debug("Debug Filter Chain...\n{}", String.join(System.lineSeparator(), filterNames));
    };
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.withDefaultRolePrefix()
        .role(Role.ADMIN.name())
        .implies(Role.USER.name(), Role.CHANNEL_MANAGER.name())

        .role(Role.CHANNEL_MANAGER.name())
        .implies(Role.USER.name())

        .build();
  }

  @Bean
  static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
      RoleHierarchy roleHierarchy) {
    DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    return handler;
  }

  @Bean
  public JwtRegistry jwtRegistry(JwtTokenProvider jwtTokenProvider) {
    return new InMemoryJwtRegistry(1, jwtTokenProvider);
  }
}