package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;
import lombok.Getter;

/**
 * 애플리케이션 공통 예외 기본 클래스.
 * 모든 도메인별 예외(UserException, ChannelException 등)가 이 클래스를 상속한다.
 * details 맵에 예외 발생 상황의 추가 정보(조회 시도한 ID 등)를 담는다.
 */
@Getter
public class DiscodeitException extends RuntimeException {

    private final Instant timestamp;
    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    public DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode.getMessage());
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = details;
    }
}
