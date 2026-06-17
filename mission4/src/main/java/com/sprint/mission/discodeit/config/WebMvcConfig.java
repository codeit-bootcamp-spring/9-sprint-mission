package com.sprint.mission.discodeit.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

  private final MDCLoggingInterceptor mdcLoggingInterceptor;

  @Value("${discodeit.storage.local.root-path}")
  private String rootPath;


  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(mdcLoggingInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns("/css/**", "/js/**", "/favicon.ico");
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // 프론트엔드가 /storage/** 경로로 요청하면 rootPath 폴더의 파일을 서빙합니다
    registry.addResourceHandler("/storage/**")
        .addResourceLocations("file:" + rootPath + "/");
  }
}