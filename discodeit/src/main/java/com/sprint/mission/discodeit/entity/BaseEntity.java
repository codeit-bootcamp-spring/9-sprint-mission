package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * 모든 도메인(User/Channel/Message)이 공통으로 가져야 하는 필드 묶음
 * - id: UUID
 * - createdAt / updatedAt: unix timestamp(ms)
 */
@Getter
public abstract class BaseEntity {
    // private = 외부에서 직접 접근/수정 못하게 캡슐화
    // final = 한 번 초기화되면 “절대 바뀌면 안 되는 값” (id, createdAt은 변경되면 안 됨)
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    protected BaseEntity() {
        Instant now = Instant.now();
        this.id = UUID.randomUUID();
        this.createdAt = now;
        this.updatedAt = now;
    }
    // Getter(게터) = 필드 값을 “읽기 전용으로 꺼내는 함수”
    // 왜 필드를 public으로 안 두고 getter로 읽게 만드냐?
    // - 필드를 외부에서 마음대로 바꾸지 못하게 하려고(캡슐화)

    // 파일 저장소에서 복원할 때 사용
    protected BaseEntity(UUID id, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * protected = 외부가 “막 updatedAt만 갱신”하는 걸 방지
     * updateTimestamp() = "수정 시간이 바뀌어야 하는 순간"에만 자식 도메인 내부에서 호출
     */
    protected void touch() {
        this.updatedAt = Instant.now();
    }
}