package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity {

  @Column(length = 50, nullable = false, unique = true)
  private String username;

  @Column(length = 100, nullable = false, unique = true)
  private String email;

  @Column(length = 60, nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private UserRole role;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "profile_id", columnDefinition = "uuid")
  private BinaryContent profile;

  @Column(name = "initial_admin", nullable = false)
  private boolean initialAdmin;

  public User(String username, String email, String password, BinaryContent profile) {
    this(username, email, password, UserRole.USER, profile);
  }

  public User(String username, String email, String password, UserRole role, BinaryContent profile) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.role = role;
    this.profile = profile;
  }

  public void update(String username, String email, String password, BinaryContent profile) {
    if (username != null && !username.equals(this.username)) {
      this.username = username;
    }
    if (email != null && !email.equals(this.email)) {
      this.email = email;
    }
    if (password != null && !password.equals(this.password)) {
      this.password = password;
    }
    if (profile != null) {
      this.profile = profile;
    }
  }

  public void updateRole(UserRole role) {
    if (role != null && role != this.role) {
      this.role = role;
    }
  }

  public void markInitialAdmin() {
    this.initialAdmin = true;
  }
}
