package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import java.util.UUID;

/**
 * 사용자 엔티티 클래스
 * 시스템의 사용자 정보를 나타내며 인증 및 프로필 정보를 포함합니다.
 */
@Getter
public class User extends BaseEntity {
    /** 사용자명 */
    private String username;

    /** 이메일 주소 */
    private String email;

    /** 비밀번호 */
    private String password;

    /** 프로필 이미지 ID (BinaryContent 참조, null 가능) */
    private UUID profileId;

    /**
     * 사용자 생성자
     *
     * @param username 사용자명
     * @param email 이메일 주소
     * @param password 비밀번호
     * @param profileId 프로필 이미지 ID (null 가능)
     */
    public User(String username, String email, String password, UUID profileId) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
    }


    /**
     * 사용자 정보를 업데이트합니다.
     * null이 아니고 기존 값과 다른 경우에만 업데이트되며, 실제 변경이 있을 때만 수정일시가 갱신됩니다.
     *
     * @param username 새로운 사용자명 (null이면 업데이트하지 않음)
     * @param email 새로운 이메일 (null이면 업데이트하지 않음)
     * @param password 새로운 비밀번호 (null이면 업데이트하지 않음)
     * @param profileId 새로운 프로필 이미지 ID (null이면 업데이트하지 않음)
     */
    public void update(String username, String email, String password, UUID profileId) {
        boolean updated = false;
        if (username != null && !username.equals(this.username)) {
            this.username = username;
            updated = true;
        }
        if (email != null && !email.equals(this.email)) {
            this.email = email;
            updated = true;
        }
        if (password != null && !password.equals(this.password)) {
            this.password = password;
            updated = true;
        }
        if (profileId != null && !profileId.equals(this.profileId)) {
            this.profileId = profileId;
            updated = true;
        }
        if (updated) updateTimeStamp();
    }
}