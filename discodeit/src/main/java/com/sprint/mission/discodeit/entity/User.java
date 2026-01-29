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
    private String displayName;
    private String email;
    private String phoneNumber;
    private final Instant createdAt;
    private Instant updatedAt;

    // Binary 참조 (프로필 대표 이미지)
    private UUID profileImageId;

    //생성자 생성
    public User(String displayName, String email, String phoneNumber) {
        Instant now = Instant.now();
        this.id = UUID.randomUUID();
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.createdAt = now;
        this.updatedAt = now;
        this.profileImageId = null;
    }

    public void update(String displayName, String email, String phoneNumber) {
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
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
}
