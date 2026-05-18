package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.RememberMeConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final LoginSuccessHandler loginSuccessHandler;
  private final LoginFailureHandler loginFailureHandler;

  private final UserDetailsService userDetailsService;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(this::configureCsrf)
        .formLogin(this::configureFormLogin)
        .logout(this::configureLogout)
        .authorizeHttpRequests(this::configureAuthorizeRequests)
        .exceptionHandling(this::configureExceptionHandling)
        .sessionManagement(this::configureSessionManagement)
        .rememberMe(this::configureRememberMe);

    return http.build();
  }


  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
    hierarchy.setHierarchy("ROLE_ADMIN > ROLE_CHANNEL_MANAGER\n" +
        "ROLE_CHANNEL_MANAGER > ROLE_USER");
    return hierarchy;
  }

  @Bean
  static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
    DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    return handler;
  }

  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }

  private void configureCsrf(CsrfConfigurer<HttpSecurity> csrf) {
    csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler());
  }

  private void configureFormLogin(FormLoginConfigurer<HttpSecurity> login) {
    login.loginProcessingUrl("/api/auth/login")
        .successHandler(loginSuccessHandler)
        .failureHandler(loginFailureHandler);
  }

  private void configureLogout(LogoutConfigurer<HttpSecurity> logout) {
    logout.logoutUrl("/api/auth/logout")
        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
        .invalidateHttpSession(true)
        .deleteCookies("JSESSIONID");
  }

  private void configureAuthorizeRequests(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
    auth.requestMatchers("/index.html", "/*.ico", "/assets/**").permitAll() // 정적 리소스 권한
        .requestMatchers(HttpMethod.GET, "/api/auth/csrf-token").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
        .requestMatchers("/api/auth/login", "/api/auth/logout").permitAll()
        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
        .requestMatchers("/actuator/**").permitAll()
        .requestMatchers("/", "/error").permitAll()
        .anyRequest().authenticated();
  }

  private void configureExceptionHandling(ExceptionHandlingConfigurer<HttpSecurity> ex) {
    ex.authenticationEntryPoint((request, response, authException) -> {
          response.setStatus(HttpStatus.UNAUTHORIZED.value());
          response.setContentType(MediaType.APPLICATION_JSON_VALUE);
          response.setCharacterEncoding("UTF-8");
          response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"로그인 필요\"}");
        })
        .accessDeniedHandler((request, response, accessDeniedException) -> {
          response.setStatus(HttpStatus.FORBIDDEN.value());
          response.setContentType(MediaType.APPLICATION_JSON_VALUE);
          response.setCharacterEncoding("UTF-8");
          response.getWriter().write("{\"error\": \"Forbidden\", \"message\": \"접근 권한 없음\"}");
        });
  }

  private void configureSessionManagement(SessionManagementConfigurer<HttpSecurity> session) {
    session.maximumSessions(1)
        .maxSessionsPreventsLogin(false)
        .sessionRegistry(sessionRegistry());
  }

  private void configureRememberMe(RememberMeConfigurer<HttpSecurity> rememberMe) {
    rememberMe.key("my-super-secret-key-discodeit")
        .tokenValiditySeconds(604800)
        .userDetailsService(userDetailsService)
        .rememberMeParameter("remember-me");
  }

}
