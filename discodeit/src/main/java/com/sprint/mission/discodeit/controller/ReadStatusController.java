package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {

	private final ReadStatusService readStatusService;

	@Operation(summary = "Message 읽음 상태 생성")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Message 읽음 상태가 성공적으로 생성됨",
					content = @Content(schema = @Schema(implementation = ReadStatus.class))),
			@ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "400", description = "이미 읽음 상태가 존재함",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PostMapping
	public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest request) {
		ReadStatus readStatus = readStatusService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
	}

	@Operation(summary = "User의 Message 읽음 상태 목록 조회")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReadStatus.class))))
	})
	@GetMapping
	public ResponseEntity<List<ReadStatus>> findAllByUserId(
			@RequestParam UUID userId
	) {
		List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
		return ResponseEntity.ok(readStatuses);
	}

	@Operation(summary = "Message 읽음 상태 수정")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Message 읽음 상태가 성공적으로 수정됨",
					content = @Content(schema = @Schema(implementation = ReadStatus.class))),
			@ApiResponse(responseCode = "404", description = "Message 읽음 상태를 찾을 수 없음",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PatchMapping("/{readStatusId}")
	public ResponseEntity<ReadStatus> update(
			@PathVariable UUID readStatusId,
			@RequestBody ReadStatusUpdateRequest request
	) {
		ReadStatus readStatus = readStatusService.update(readStatusId, request);
		return ResponseEntity.ok(readStatus);
	}
}
