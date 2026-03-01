package com.sprint.mission.discordit.global;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResult<T> {

  private boolean success;
  private T data;
  private ApiError error;
  private Instant timestamp;

  public static <T> ApiResult<T> success(T data) {
    return new ApiResult<>(true, data, null, Instant.now());
  }

  public static <T> ApiResult<T> error(ApiError error) {
    return new ApiResult<>(false, null, error, Instant.now());
  }
}

