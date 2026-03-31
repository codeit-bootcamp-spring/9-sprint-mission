package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserApi {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @Override
  public ResponseEntity<List<UserDto>> findAll() {
    log.debug("Fetching all users list");
    return ResponseEntity.ok(userService.findAll());
  }

  @Override
  public ResponseEntity<UserDto> register(@Valid UserCreateRequest request, MultipartFile profile,
      HttpSession session) {
    log.info("Registering new user with email: {}", request.email());
    BinaryContentCreateRequest profileRequest = resolveProfileRequest(profile);

    UserDto userDto = userService.create(request, profileRequest);
    session.setAttribute("USER_ID", userDto.id());

    log.info("User registered successfully. ID: {}", userDto.id());
    return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
  }

  @Override
  public ResponseEntity<UserDto> update(@PathVariable UUID userId, @Valid UserUpdateRequest request,
      MultipartFile profile) {
    log.info("Updating user information for ID: {}", userId);
    BinaryContentCreateRequest profileRequest = resolveProfileRequest(profile);
    return ResponseEntity.ok(userService.update(userId, request, profileRequest));
  }

  @Override
  public ResponseEntity<UserStatusDto> updateStatus(@PathVariable UUID userId,
      @Valid UserStatusUpdateRequest request) {
    log.debug("Updating status for user: {}, last active: {}", userId, request.newLastActiveAt());
    return ResponseEntity.ok(userStatusService.updateByUserId(userId, request));
  }

  @Override
  public ResponseEntity<Void> delete(@PathVariable UUID userId) {
    log.info("Deleting user ID: {}", userId);
    userService.delete(userId);
    return ResponseEntity.noContent().build();
  }

  private BinaryContentCreateRequest resolveProfileRequest(MultipartFile profileFile) {
    if (profileFile == null || profileFile.isEmpty()) {
      return null;
    }
    try {
      log.debug("Processing profile image: {}", profileFile.getOriginalFilename());
      return new BinaryContentCreateRequest(
          profileFile.getBytes(), profileFile.getOriginalFilename(), profileFile.getContentType(),
          profileFile.getSize()
      );
    } catch (IOException e) {
      log.error("Failed to process profile image", e);
      throw new RuntimeException("이미지 처리 중 오류 발생", e);
    }
  }
}