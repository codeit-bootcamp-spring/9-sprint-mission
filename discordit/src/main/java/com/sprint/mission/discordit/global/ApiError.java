package com.sprint.mission.discordit.global;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiError {

  private String code;
  private String message;
  private List<ValidationError> details;
}
