package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

@Tag(name = "User", description = "User API")
public interface UserApi {

  @Operation(summary = "전체 User 목록 조회", operationId = "findAll")
  @GetMapping("/api/users")
  ResponseEntity<List<UserDto>> findAll();

  @Operation(summary = "User 등록 (가입 즉시 자동 로그인)", operationId = "create")
  @PostMapping(value = "/api/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<UserDto> register(
      @RequestPart("userCreateRequest") UserCreateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile,
      HttpSession session);

  @Operation(summary = "User 삭제", operationId = "delete")
  @DeleteMapping("/api/users/{userId}")
  ResponseEntity<Void> delete(@Parameter(description = "삭제할 User ID") @PathVariable UUID userId);

  @Operation(summary = "User 정보 수정", operationId = "update")
  @PatchMapping(value = "/api/users/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<UserDto> update(
      @Parameter(description = "수정할 User ID") @PathVariable UUID userId,
      @RequestPart("userUpdateRequest") UserUpdateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile);

  @Operation(summary = "User 온라인 상태 업데이트", operationId = "updateUserStatusByUserId")
  @PatchMapping("/api/users/{userId}/userStatus")
  ResponseEntity<UserStatusDto> updateStatus(
      @Parameter(description = "상태를 변경할 User ID") @PathVariable UUID userId,
      @RequestBody UserStatusUpdateRequest request);
}