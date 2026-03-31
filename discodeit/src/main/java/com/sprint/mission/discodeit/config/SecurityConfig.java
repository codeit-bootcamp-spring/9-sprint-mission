package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http

        .csrf(AbstractHttpConfigurer::disable)

        .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))

        .authorizeHttpRequests(auth -> auth

            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

            .requestMatchers("/h2-console/**").permitAll()

            .requestMatchers("/actuator/**").permitAll()

            .anyRequest().permitAll()
        );

    return http.build();
  }
}