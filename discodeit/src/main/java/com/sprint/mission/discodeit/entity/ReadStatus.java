package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
// 'read_statuses' 테이블과 연결하며, 동일한 유저가 동일한 채널을 중복해서 읽음 상태를 가지지 않도록 유니크 제약조건을 겁니다.
@Table(name = "read_statuses", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "channel_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadStatus extends BaseUpdatableEntity {

    // 특정 유저와의 다대일 관계를 매핑합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 특정 채널과의 다대일 관계를 매핑합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    // 마지막으로 읽은 시간을 저장하는 컬럼입니다.
    @Column(name = "last_read_at", nullable = false)
    private Instant lastReadAt;

    // 객체 생성 시 초기값을 세팅하는 생성자입니다.
    public ReadStatus(User user, Channel channel) {
        this.user = user;
        this.channel = channel;
        this.lastReadAt = Instant.now();
    }

    // 읽음 시간을 현재 시간으로 갱신하는 편의 메서드입니다.
    public void update() {
        this.lastReadAt = Instant.now();
    }
}