package com.sprint.mission.discodeit.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

/**
 * 전역 예외 처리 핸들러
 *
 * 모든 컨트롤러에서 발생하는 예외를 중앙에서 처리하여
 * 일관된 에러 응답을 제공합니다.
 */
@RestControllerAdvice
@ApiResponses({
		@ApiResponse(responseCode = "500", description = "서버 내부 오류",
				content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
})
public class GlobalExceptionHandler {

	/**
	 * NoSuchElementException 처리
	 * - 존재하지 않는 리소스 조회 시 발생
	 * - 404 NOT_FOUND 반환
	 */
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<ErrorResponse> handleNoSuchElement(NoSuchElementException e) {
		ErrorResponse error = ErrorResponse.of(e.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	/**
	 * IllegalArgumentException 처리
	 * - 잘못된 요청 파라미터, 비즈니스 로직 위반 시 발생
	 * - 400 BAD_REQUEST 반환
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
		ErrorResponse error = ErrorResponse.of(e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	/**
	 * 기타 모든 예외 처리
	 * - 예상하지 못한 서버 에러
	 * - 500 INTERNAL_SERVER_ERROR 반환
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception e) {
		ErrorResponse error = ErrorResponse.of("서버 내부 오류가 발생했습니다: " + e.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
}
