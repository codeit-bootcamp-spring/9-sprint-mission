package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @Override
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserDto> userDtos = userService.findAll().stream()
        .map(this::convertToDto)
        .toList();
    return ResponseEntity.ok(userDtos);
  }

  @Override
  public ResponseEntity<UserDto> register(
      @Valid @RequestPart("userCreateRequest") UserCreateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile,
      HttpSession session) {

    Optional<BinaryContentCreateRequest> profileRequest = resolveProfileRequest(profile);

    return userService.create(request, profileRequest)
        .map(user -> {
          session.setAttribute("USER_ID", user.getId());
          return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(user));
        })
        .orElse(ResponseEntity.badRequest().build());
  }

  @Override
  public ResponseEntity<UserDto> update(
      @PathVariable UUID userId,
      @Valid @RequestPart("userUpdateRequest") UserUpdateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {

    Optional<BinaryContentCreateRequest> profileRequest = resolveProfileRequest(profile);
    return userService.update(userId, request, profileRequest)
        .map(user -> ResponseEntity.ok(convertToDto(user)))
        .orElse(ResponseEntity.notFound().build());
  }

  @Override
  public ResponseEntity<UserStatusDto> updateStatus(
      @PathVariable UUID userId,
      @Valid @RequestBody UserStatusUpdateRequest request) {

    UserStatus updatedStatus = userStatusService.updateByUserId(userId, request);
    return ResponseEntity.ok(new UserStatusDto(
        updatedStatus.getId(), updatedStatus.getUserId(), updatedStatus.getLastActiveAt()
    ));
  }

  @Override
  public ResponseEntity<Void> delete(@PathVariable UUID userId) {
    if (userService.delete(userId)) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }

  private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profileFile) {
    if (profileFile == null || profileFile.isEmpty()) {
      return Optional.empty();
    }
    try {
      return Optional.of(new BinaryContentCreateRequest(
          profileFile.getBytes(),
          profileFile.getContentType(),
          profileFile.getOriginalFilename(),
          profileFile.getSize()
      ));
    } catch (IOException e) {
      throw new RuntimeException("프로필 이미지 처리 중 오류가 발생했습니다.", e);
    }
  }

  private UserDto convertToDto(User user) {
    return new UserDto(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        user.getProfileId(),
        userStatusService.isUserOnline(user.getId())
    );
  }
}