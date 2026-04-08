package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 애플리케이션 전체 에러 코드 정의.
 * 각 코드는 HTTP 상태코드와 사용자 메시지를 포함한다.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"),
    DUPLICATE_USER(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다"),

    // Channel
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "채널을 찾을 수 없습니다"),
    PRIVATE_CHANNEL_UPDATE(HttpStatus.BAD_REQUEST, "비공개 채널은 수정할 수 없습니다"),

    // Message
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "메시지를 찾을 수 없습니다"),

    // BinaryContent
    BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다"),

    // ReadStatus
    READ_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "읽음 상태를 찾을 수 없습니다"),

    // UserStatus
    USER_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 상태를 찾을 수 없습니다"),
    DUPLICATE_USER_STATUS(HttpStatus.CONFLICT, "이미 사용자 상태가 존재합니다"),

    // Auth
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다"),

    // Validation
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "입력값 검증에 실패했습니다");

    private final HttpStatus status;
    private final String message;
}
