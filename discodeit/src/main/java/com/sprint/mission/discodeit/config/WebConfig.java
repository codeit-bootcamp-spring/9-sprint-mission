package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.interceptor.MDCLoggingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final ObjectMapper objectMapper;
  private final MDCLoggingInterceptor mdcLoggingInterceptor;

  public WebConfig(ObjectMapper objectMapper, MDCLoggingInterceptor mdcLoggingInterceptor) {
    this.objectMapper = objectMapper;
    this.mdcLoggingInterceptor = mdcLoggingInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {

    registry.addInterceptor(mdcLoggingInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns("/", "/index.html", "/static/**", "/css/**", "/js/**", "/favicon.ico",
            "/actuator/**");
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/{path:[^\\.]*}")
        .setViewName("forward:/index.html");
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/**")
        .addResourceLocations("classpath:/static/");
  }
}