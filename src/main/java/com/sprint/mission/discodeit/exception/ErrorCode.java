package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다"),
    USER_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 사용 중인 값입니다"),
    DUPLICATE_USER(HttpStatus.CONFLICT, "이미 존재하는 유저입니다"),  // ← 추가
    INVALID_USER_CREDENTIALS(HttpStatus.UNAUTHORIZED, "잘못된 인증 정보입니다"),

    // Channel
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 채널입니다"),
    CHANNEL_UPDATE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "Private 채널은 수정할 수 없습니다"),

    // Message
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 메시지입니다"),

    // BinaryContent
    BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 파일입니다"),

    // ReadStatus
    READ_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 읽음 상태입니다"),
    DUPLICATE_READ_STATUS(HttpStatus.CONFLICT, "이미 존재하는 읽음 상태입니다"),

    // UserStatus
    USER_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저 상태입니다"),
    DUPLICATE_USER_STATUS(HttpStatus.CONFLICT, "이미 존재하는 유저 상태입니다"),

    // 기타
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다");

    private final HttpStatus status;
    private final String message;
}
