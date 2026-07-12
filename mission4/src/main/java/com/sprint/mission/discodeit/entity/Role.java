package com.sprint.mission.discodeit.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
  ADMIN("ROLE_ADMIN", "관리자"),
  CHANNEL_MANAGER("ROLE_CHANNEL_MANAGER", "채널 매니저"),
  USER("ROLE_USER", "일반 사용자");

  private final String key;
  private final String title;
}
