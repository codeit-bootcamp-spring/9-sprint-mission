package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Collections;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final ObjectMapper objectMapper;

  public WebConfig(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  // [수정] 프론트엔드 규격에 맞춰 PathVariable을 유지하고 잘못된 주석을 삭제했습니다.
  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    // 프론트엔드(React)가 JSON을 application/octet-stream으로 보낼 때
    // Jackson이 이를 가로채서 DTO로 바인딩할 수 있게 해주는 "패치"입니다.
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(
        objectMapper);
    converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
    converters.add(converter);
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/assets/**")
        .addResourceLocations("classpath:/static/assets/");
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOriginPatterns(
            "http://localhost:*",
            "https://*.up.railway.app"
        )
        .allowedMethods("GET", "POST", "PATCH", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true);
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    // 1. 루트 경로 요청 시 index.html로 연결 (회로의 메인 스위치)
    registry.addViewController("/").setViewName("forward:/index.html");

    // 2. 프론트엔드 라우팅 경로들을 모두 index.html로 포워딩 (SPA 전용 브릿지)
    // 사용자가 /login, /channels 등으로 직접 접속해도 index.html이 먼저 뜨게 합니다.
    registry.addViewController("/{path:[^\\.]*}")
        .setViewName("forward:/index.html");
    registry.addViewController("/**/{path:[^\\.]*}")
        .setViewName("forward:/index.html");
  }
}