package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
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

@Tag(name = "User", description = "User API")
public interface UserApi {

  @Operation(summary = "전체 User 목록 조회")
  @GetMapping
  ResponseEntity<List<UserDto>> findAll();

  @Operation(summary = "User 등록")
  @PostMapping(consumes = "multipart/form-data")
  ResponseEntity<UserDto> register(
      @Valid @RequestPart("userCreateRequest") UserCreateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile,
      HttpSession session
  );

  @Operation(summary = "User 정보 수정")
  @PatchMapping(value = "/{userId}", consumes = "multipart/form-data")
  ResponseEntity<UserDto> update(
      @PathVariable(name = "userId") UUID userId,
      @Valid @RequestPart("userUpdateRequest") UserUpdateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  );

  @Operation(summary = "User 온라인 상태 업데이트")
  @PatchMapping("/{userId}/userStatus")
  ResponseEntity<UserStatusDto> updateStatus(
      @PathVariable(name = "userId") UUID userId,
      @Valid @RequestBody UserStatusUpdateRequest request
  );

  @Operation(summary = "User 삭제")
  @DeleteMapping("/{userId}")
  ResponseEntity<Void> delete(@PathVariable(name = "userId") UUID userId);
}