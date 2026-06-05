package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.exception.User.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.security.filter.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.security.handler.JwtLoginSuccessHandler;
import com.sprint.mission.discodeit.security.handler.JwtLogoutHandler;
import com.sprint.mission.discodeit.security.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.security.handler.SpaCsrfTokenRequestHandler;
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
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;
  private final JwtRegistry jwtRegistry;

  public SecurityConfig(UserRepository userRepository, JwtTokenProvider jwtTokenProvider,
      JwtRegistry jwtRegistry) {
    this.userRepository = userRepository;
    this.jwtTokenProvider = jwtTokenProvider;
    this.jwtRegistry = jwtRegistry;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http,
      LoginFailureHandler loginFailureHandler,
      JwtLogoutHandler jwtLogoutHandler,
      JwtLoginSuccessHandler jwtLoginSuccessHandler
      , UserDetailsService userDetailsService) throws Exception {
    http
        .csrf(csrf -> csrf.
            csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
            .ignoringRequestMatchers("/api/auth/login")
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.PUT, "/api/auth/role").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/api/binaryContents/*/downlad").permitAll()
            .requestMatchers(
                "/",
                "/login",
                "/api/auth/csrf-token",
                "/api/auth/login",
                "/api/auth/logout",
                "/api/auth/refresh",
                "/api/auth/me",
                "/api/users",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/actuator/**",
                "/favicon.ico",
                "/index.html",
                "/*.html",
                "/assets/**",
                "/error"
            ).permitAll()
            .anyRequest().authenticated()
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(
            new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService, jwtRegistry),
            UsernamePasswordAuthenticationFilter.class)
        .formLogin(login -> login.loginProcessingUrl("/api/auth/login")
            .successHandler(jwtLoginSuccessHandler)
            .failureHandler(loginFailureHandler))

        .logout(logout -> logout.logoutUrl("/api/auth/logout")
            .addLogoutHandler(jwtLogoutHandler)
            .logoutSuccessHandler(
                new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
            .invalidateHttpSession(true)
            .deleteCookies("REFRESH_TOKEN"))
        .httpBasic(basic -> basic.disable())
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            .accessDeniedHandler((request, response, accessDeniedException) -> {
              response.setStatus(HttpStatus.FORBIDDEN.value());
              response.setContentType("application/json;charset=UTF-8");
              response.getWriter().write("{\"error\": \"Forbidden\", \"message\": \"권한이 부족합니다.\"}");
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

    return RoleHierarchyImpl.fromHierarchy("""
        ROLE_ADMIN > ROLE_CHANNEL_MANAGER
        ROLE_CHANNEL_MANAGER > ROLE_USER
        """);
  }

  @Bean
  static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
      RoleHierarchy roleHierarchy) {
    DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    return handler;
  }

  @Bean
  public org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring()
        .requestMatchers(
            org.springframework.boot.autoconfigure.security.servlet.PathRequest
                .toStaticResources().atCommonLocations()
        );
  }


  @Bean
  public UserDetailsService userDetailsService() {
    return username -> userRepository.findByUsername(username)
        .map(DiscodeitUserDetails::new)
        .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다: " + username));
  }


}
