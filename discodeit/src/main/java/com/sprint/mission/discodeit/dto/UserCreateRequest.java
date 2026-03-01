package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {

  private String username;
  private String email;
  private String password;
  private String phoneNumber;
}