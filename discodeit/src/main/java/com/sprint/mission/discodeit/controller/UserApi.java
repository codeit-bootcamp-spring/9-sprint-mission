package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@Tag(name = "User", description = "유저 관리 API")
public interface UserApi {

  @Operation(summary = "전체 유저 목록 조회")
  @GetMapping
  ResponseEntity<List<UserDto>> findAll();

  @Operation(summary = "회원 가입")
  @PostMapping(consumes = "multipart/form-data")
  ResponseEntity<UserDto> register(
      @Valid @RequestPart("userCreateRequest") UserCreateRequest request, // 파트명 수정
      @RequestPart(value = "profile", required = false) MultipartFile profile,
      HttpSession session
  );

  @Operation(summary = "유저 정보 수정")
  @PatchMapping(value = "/{userId}", consumes = "multipart/form-data")
  ResponseEntity<UserDto> update(
      @PathVariable UUID userId,
      @Valid @RequestPart("userUpdateRequest") UserUpdateRequest request, // 파트명 수정
      @RequestPart(value = "profile", required = false) MultipartFile profile
  );

  @Operation(summary = "유저 상태 업데이트")
  @PatchMapping("/{userId}/userStatus")
  ResponseEntity<UserStatusDto> updateStatus(
      @PathVariable UUID userId,
      @Valid @RequestBody UserStatusUpdateRequest request
  );

  @Operation(summary = "유저 삭제")
  @DeleteMapping("/{userId}")
  ResponseEntity<Void> delete(@PathVariable UUID userId);
}