package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * 모든 엔티티의 공통 속성을 정의하는 기본 엔티티 클래스
 * ID, 생성일시, 수정일시를 포함하며 직렬화를 지원합니다.
 */
@Getter
public class BaseEntity implements Serializable {
    /** 직렬화 버전 UID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 엔티티 고유 식별자 (UUID) */
    protected UUID id;

    /** 엔티티 생성 일시 */
    protected Instant createdAt;

    /** 엔티티 마지막 수정 일시 */
    protected Instant updatedAt;

    /**
     * 기본 생성자
     * UUID를 생성하고 생성일시와 수정일시를 현재 시간으로 초기화합니다.
     */
    public BaseEntity() {
        this.id = UUID.randomUUID();
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }


    /**
     * 엔티티 수정 시 수정일시를 현재 시간으로 업데이트합니다.
     */
    protected void updateTimeStamp() {
        this.updatedAt = Instant.now();
    }
}