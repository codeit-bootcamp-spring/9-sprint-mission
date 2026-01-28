package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.UUID;
/**
 * User 도메인
 * - 닉네임은 null이면 username으로 대체
 */

@Getter
public class User extends BaseEntity {

    private final String loginId;   // 로그인용 아이디
    private String password;        // 비밀번호 (변경가능)
    private final String username;  // 실명
    private String nickname;        // 닉네임(없으면 username으로 대체) *선택사항
    private String phoneNumber;     // 전화번호(변경 가능)

    public User(String loginId, String password, String username, String phoneNumber, String nickname) {
        super();

        this.loginId = loginId;
        this.password = password;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.nickname = (nickname != null) ? nickname : username;
    }

    public User(UUID id, long createdAt, long updatedAt, String loginId, String password, String username, String phoneNumber, String nickname) {
        super(id, createdAt, updatedAt);

        this.loginId = loginId;
        this.password = password;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.nickname = (nickname != null) ? nickname : username;
    }
    /**
     * update(...)로 수정 항목을 한 번에 받는 이유:
     * - setNickname(), setPhoneNumber() 등 setter 남발하면
     *   “수정 시간 갱신” 같은 공통 로직이 흩어지기 쉬움
     * - update에서 한번에 처리하면 수정 정책이 한 군데에 모임
     *
     * null 체크를 하는 이유:
     * - 서비스에서 "일부만 수정"하는 경우가 많음
     * - null은 "이번 수정에서 건드리지 않겠다"로 해석 가능
     */

    public void update(String nickname, String phoneNumber, String password) {
        // null 방어
        if (nickname != null) this.nickname = nickname;
        if (phoneNumber != null) this.phoneNumber = phoneNumber;
        if (password != null) this.password = password;

        // 수정이 일어나면 updatedAt 갱신
        updateTimestamp();
    }
}
