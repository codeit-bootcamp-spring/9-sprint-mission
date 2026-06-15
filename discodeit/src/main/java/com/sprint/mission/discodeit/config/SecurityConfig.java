package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.secure.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.secure.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.secure.handler.JwtLoginSuccessHandler;
import com.sprint.mission.discodeit.secure.handler.JwtLogoutHandler;
import com.sprint.mission.discodeit.secure.handler.LoginFailureHandler;
import com.sprint.mission.discodeit.secure.handler.SpaCsrfTokenRequestHandler;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;


@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final LoginFailureHandler loginFailureHandler;
  private final DiscodeitUserDetailsService userDetailsService;
  private final JwtLoginSuccessHandler jwtLoginSuccessHandler;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtLogoutHandler jwtLogoutHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
            .ignoringRequestMatchers(
                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/auth/login"),
                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/auth/logout"))
        )
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            .successHandler(jwtLoginSuccessHandler)
            .failureHandler(loginFailureHandler)
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .addLogoutHandler(jwtLogoutHandler)
            .logoutSuccessHandler(
                new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .permitAll()
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/users"),
                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/auth/csrf-token"),
                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/auth/refresh"),
                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/auth/login"),
                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/auth/logout"),
                new NegatedRequestMatcher(AntPathRequestMatcher.antMatcher("/api/**"))
            ).permitAll()
            .anyRequest().authenticated()
        )
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint((request, response, e) -> {
              response.setStatus(HttpStatus.UNAUTHORIZED.value());
              response.setContentType("application/json; charset=UTF-8");
              response.getWriter().write("""
                  {
                    "error": "UNAUTHORIZED",
                    "message": "인증이 필요합니다."
                  }
                  """);
            })
            .accessDeniedHandler((request, response, e) -> {
              response.setStatus(HttpStatus.FORBIDDEN.value());
              response.setContentType("application/json; charset=UTF-8");
              response.getWriter().write("""
                  {
                    "error": "FORBIDDEN",
                    "message": "접근 권한이 없습니다."
                  }
                  """);
            })
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .addFilterBefore(
            jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class);

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
        .role("ADMIN").implies("CHANNEL_MANAGER")
        // "ADMIN이 CHANNEL_MANAGER를 포함한다"를 코드로 명확하게 표현
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
