package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.UUID;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class User extends BaseEntity {

  private String username;
  private String email;
  private String password;
  private String phoneNumber;
  private UUID profileId;

  public User(String username, String email, String password, String phoneNumber, UUID profileId) {
    super();
    this.username = username;
    this.email = email;
    this.password = password;
    this.phoneNumber = phoneNumber;
    this.profileId = profileId;
  }
}