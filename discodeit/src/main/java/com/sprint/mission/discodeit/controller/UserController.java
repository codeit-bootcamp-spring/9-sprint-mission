package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Tag(name = "User", description = "User API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final UserStatusService userStatusService;

	@Operation(summary = "User 등록")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨",
					content = @Content(schema = @Schema(implementation = UserResponse.class))),
			@ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
					mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
					encoding = @Encoding(name = "userCreateRequest", contentType = MediaType.APPLICATION_JSON_VALUE)
			)
	)
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UserResponse> create(
			@RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
			@RequestPart(value = "profile", required = false) MultipartFile profile
	) throws IOException {
		BinaryContentCreateRequest profileRequest = resolveProfile(profile);
		UserResponse user = userService.create(userCreateRequest, profileRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(user);
	}

	@Operation(summary = "전체 User 목록 조회")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "User 목록 조회 성공",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class))))
	})
	@GetMapping
	public ResponseEntity<List<UserDto>> findAll() {
		List<UserDto> users = userService.findAllAsDto();
		return ResponseEntity.ok(users);
	}

	@Operation(summary = "User 정보 수정")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨",
					content = @Content(schema = @Schema(implementation = UserResponse.class))),
			@ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
					mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
					encoding = @Encoding(name = "userUpdateRequest", contentType = MediaType.APPLICATION_JSON_VALUE)
			)
	)
	@PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UserResponse> update(
			@PathVariable UUID userId,
			@RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
			@RequestPart(value = "profile", required = false) MultipartFile profile
	) throws IOException {
		BinaryContentCreateRequest profileRequest = resolveProfile(profile);
		UserResponse user = userService.update(userId, userUpdateRequest, profileRequest);
		return ResponseEntity.ok(user);
	}

	@Operation(summary = "User 삭제")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨", content = @Content),
			@ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@DeleteMapping("/{userId}")
	public ResponseEntity<Void> delete(
			@PathVariable UUID userId
	) {
		userService.delete(userId);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "User 온라인 상태 업데이트")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트됨",
					content = @Content(schema = @Schema(implementation = UserStatus.class))),
			@ApiResponse(responseCode = "404", description = "해당 User의 UserStatus를 찾을 수 없음",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PatchMapping(value = "/{userId}/userStatus", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserStatus> updateUserStatus(
			@PathVariable UUID userId,
			@RequestBody UserStatusUpdateRequest request
	) {
		UserStatus userStatus = userStatusService.updateByUserId(userId, request);
		return ResponseEntity.ok(userStatus);
	}

	private BinaryContentCreateRequest resolveProfile(MultipartFile profile) throws IOException {
		if (profile == null || profile.isEmpty()) {
			return null;
		}
		return new BinaryContentCreateRequest(
				profile.getOriginalFilename(),
				profile.getContentType(),
				profile.getBytes()
		);
	}
}
