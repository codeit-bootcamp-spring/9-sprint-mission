package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.security.handler.LoginSuccessHandler;
import com.sprint.mission.discodeit.security.handler.SpaCsrfTokenRequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      SessionRegistry sessionRegistry
  ) throws Exception {

    http
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
        )
        .exceptionHandling(handler -> handler
            .authenticationEntryPoint(
                new org.springframework.security.web.authentication.HttpStatusEntryPoint(
                    HttpStatus.UNAUTHORIZED))
        )

        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            .successHandler(loginSuccessHandler)
            .failureHandler(loginFailureHandler)
        )

        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .logoutSuccessHandler(
                new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
        )

        .sessionManagement(management -> management
            .sessionConcurrency(concurrency -> concurrency
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .sessionRegistry(sessionRegistry)
            )
        )

        .rememberMe(rememberMe -> rememberMe
            .rememberMeParameter("remember-me")
            .tokenValiditySeconds(60 * 60 * 24 * 7)
            .alwaysRemember(false)
            .userDetailsService(userDetailsService)
            .key("discodeit-remember-me-key")
        )

        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/",
                "/index.html",
                "/login",
                "/static/**",
                "/assets/**",
                "/*.js",
                "/*.css",
                "/*.ico",
                "/*.png"
            ).permitAll()
            .requestMatchers("/api/auth/csrf-token").permitAll()
            .requestMatchers("/api/auth/login").permitAll()
            .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/users").permitAll()
            .anyRequest().authenticated()
        );

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {

    return new BCryptPasswordEncoder();
  }

  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

//  @Bean
//  public static ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
//    return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
//  }

  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }
}
