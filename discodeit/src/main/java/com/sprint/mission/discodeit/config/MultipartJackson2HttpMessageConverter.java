package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;

import java.io.IOException;

// Swagger에서 JSON인식을 못해서 추가한 클래스
// AI가 만듦
@Component
public class MultipartJackson2HttpMessageConverter extends AbstractHttpMessageConverter<Object> {
  private final ObjectMapper objectMapper;

  public MultipartJackson2HttpMessageConverter(ObjectMapper objectMapper) {
    super(MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON);
    this.objectMapper = objectMapper;
  }

  @Override
  protected boolean supports(Class<?> clazz) {
    return true;
  }

  @Override
  protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
    return objectMapper.readValue(inputMessage.getBody(), clazz);
  }

  @Override
  protected void writeInternal(Object o, org.springframework.http.HttpOutputMessage outputMessage) throws IOException {
    objectMapper.writeValue(outputMessage.getBody(), o);
  }
}
