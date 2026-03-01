package com.sprint.mission.discordit.global;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationError {

  private String field;
  private String resaon;
}
