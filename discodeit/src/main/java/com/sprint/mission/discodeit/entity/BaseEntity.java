package com.sprint.mission.discodeit.entity;

import lombok.Getter;

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
    private final long createdAt;
    private long updatedAt;

    public BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = this.updatedAt = System.currentTimeMillis();
    }
    // 복원용 생성자: 파일에서 읽어온 값으로 엔티티를 "그대로" 되살릴 때 사용
    protected BaseEntity(UUID id, long createdAt, long updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    // Getter(게터) = 필드 값을 “읽기 전용으로 꺼내는 함수”
    // 왜 필드를 public으로 안 두고 getter로 읽게 만드냐?
    // - 필드를 외부에서 마음대로 바꾸지 못하게 하려고(캡슐화)

    /**
     * protected = 외부가 “막 updatedAt만 갱신”하는 걸 방지
     * updateTimestamp() = "수정 시간이 바뀌어야 하는 순간"에만 자식 도메인 내부에서 호출
     *
     * 왜 이름을 updateTimestamp로?
     * - update()라는 이름은 “도메인 필드 수정”과 겹치기 쉬움
     * - BaseEntity는 "시간 갱신"만 담당하니까 의도를 분리
     */
    protected void updateTimestamp() {
        this.updatedAt = System.currentTimeMillis();
    }
    protected void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}