package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    //
    private String username;
    private String displayName;
    private String email;
    private String phoneNumber;
    private String password;

    // Binary 참조 (프로필 대표 이미지)
    private UUID profileImageId;

    public User(String username, String email, String phoneNumber, String password) {
        Instant now = Instant.now();
        this.id = UUID.randomUUID();
        this.username = username; // 식별 + 로그인용
        this.displayName = username; // 기존 미션과 달라서 대입함
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.createdAt = now;
        this.updatedAt = now;
        this.profileImageId = null;
    }

    // 하위 호환(기존 호출부 유지용): password 없이 생성 시 null
    public User(String username, String email, String phoneNumber) {
        this(username, email, phoneNumber, null);
    }

    public void update(String displayName, String email, String phoneNumber) {
        if (displayName != null) {
            this.displayName = displayName;
        }
        if (email != null) {
            this.email = email;
        }
        if (phoneNumber != null) {
            this.phoneNumber = phoneNumber;
        }
        this.updatedAt = Instant.now();
    }

    /**
     * 프로필 대표 이미지를 설정/해제합니다.
     * - null이면 프로필 이미지를 제거합니다.
     */
    public void changeProfileImage(UUID profileImageId) {
        this.profileImageId = profileImageId;
        this.updatedAt = Instant.now();
    }

    public void changePassword(String password) {
        this.password = password;
        this.updatedAt = Instant.now();
    }
}
