package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.io.IOException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class MultipartJsonPartReader {

  private final ObjectMapper objectMapper;
  private final Validator validator;

  public <T> T read(MultipartFile part, Class<T> type) {
    try {
      T value = objectMapper.readValue(part.getBytes(), type);
      validate(value);
      return value;
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON multipart part", e);
    }
  }

  private <T> void validate(T value) {
    Set<ConstraintViolation<T>> violations = validator.validate(value);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }
}
