package com.sprint.mission.discodeit.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

  private final ObjectProvider<MDCLoggingInterceptor> mdcLoggingInterceptorProvider;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    mdcLoggingInterceptorProvider.ifAvailable(interceptor -> {
      registry.addInterceptor(interceptor)
          .addPathPatterns("/**");
    });
  }
}
