package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  // 명세서 v1.1에 없는 phoneNumber 제거

  // 1:1 관계에서 영속성 전이와 고아 객체 제거 적용 [cite: 2026-03-05]
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "profile_id")
  private BinaryContent profile;

  // UserStatus와의 양방향 매핑 추가 (부모인 User에서 관리하기 위함) [cite: 2026-03-05]
  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private UserStatus status;

  public User(String username, String email, String password, BinaryContent profile) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.profile = profile;
  }

  // UserUpdateRequest 스펙에 맞춘 업데이트 메서드
  public void update(String username, String email, String password, BinaryContent profile) {
    if (username != null) {
      this.username = username;
    }
    if (email != null) {
      this.email = email;
    }
    if (password != null) {
      this.password = password;
    }
    this.profile = profile;
  }
}