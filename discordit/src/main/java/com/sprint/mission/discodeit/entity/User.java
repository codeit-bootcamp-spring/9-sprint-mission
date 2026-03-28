package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.Getter;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@Getter
@EntityScan
public class User implements Serializable {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(
          "yyyy년 MM월 dd일 HH시 mm분 ss초")
      .withZone(ZoneId.of("Asia/Seoul"));
  private final UUID id;
  private UUID profileId;
  private final Instant createAt;
  private Instant updateAt;
  private String name;
  private String password;
  private String email;
  private String phoneNumber;
  @Serial
  private static final long serialVersionUID = 1L;

  public User(String name, String password, String email, UUID profileId) {
    Instant now = Instant.now();
    this.profileId = profileId;
    this.id = UUID.randomUUID();
    this.name = name;
    this.password = password;
    this.email = email;
    this.createAt = now;
    this.updateAt = now;
  }

  private void setUpdateAt() {
    this.updateAt = Instant.now();
  }

  public void updateUser(String name, String password, String email,
      UUID profileId) {
    /// null checker
    boolean[] argumentsList = {
        check(name), check(password), check(email),
        check(String.valueOf(profileId))
    };
    if (argumentsList[0]) {
      setName(name);
    }
    if (argumentsList[1]) {
      setPassword(password);
    }
    if (argumentsList[2]) {
      setEmail(email);
    }
    if (argumentsList[3]) {
      setProfileId(profileId);
    }
    setUpdateAt();
  }

  private boolean check(String text) {
    return text != null && !text.trim().isEmpty();
  }

  private void setName(String name) {
    this.name = name;
  }

  private void setPassword(String password) {
    this.password = password;
  }

  private void setEmail(String email) {
    this.email = email;
  }

  private void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  private void setProfileId(UUID profileId) {
    this.profileId = profileId;
  }

  @Override
  public String toString() {
    return "사용자ID : " + this.getId()
        + "\n사용자명 : " + this.getName()
        + "\n이메일 : " + this.getEmail()
        + "\n전화번호 : " + this.getPhoneNumber()
        + "\n생성일 : " + FORMATTER.format(this.getCreateAt())
        + "\n수정일 : " + FORMATTER.format(this.getUpdateAt());
  }
}
