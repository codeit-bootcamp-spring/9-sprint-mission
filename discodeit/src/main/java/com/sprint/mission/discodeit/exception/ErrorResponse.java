package com.sprint.mission.discodeit.exception;

import java.time.Instant;

/**
 * API 에러 응답 DTO
 *
 * @param message 에러 메시지
 * @param timestamp 에러 발생 시각
 */
public record ErrorResponse(
		String message,
		Instant timestamp
) {
	public static ErrorResponse of(String message) {
		return new ErrorResponse(message, Instant.now());
	}
}
