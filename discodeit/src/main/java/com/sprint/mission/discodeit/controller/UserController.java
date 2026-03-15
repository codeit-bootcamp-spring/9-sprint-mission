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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserApi {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @Override
  public ResponseEntity<List<UserDto>> findAll() {
    return ResponseEntity.ok(userService.findAll());
  }

  @Override
  public ResponseEntity<UserDto> register(UserCreateRequest request, MultipartFile profile,
      HttpSession session) {
    BinaryContentCreateRequest profileRequest = resolveProfileRequest(profile);
    UserDto userDto = userService.create(request, profileRequest);
    session.setAttribute("USER_ID", userDto.id());
    return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
  }

  @Override
  public ResponseEntity<UserDto> update(UUID userId, UserUpdateRequest request,
      MultipartFile profile) {
    BinaryContentCreateRequest profileRequest = resolveProfileRequest(profile);
    return ResponseEntity.ok(userService.update(userId, request, profileRequest));
  }

  @Override
  public ResponseEntity<UserStatusDto> updateStatus(UUID userId, UserStatusUpdateRequest request) {
    return ResponseEntity.ok(userStatusService.updateByUserId(userId, request));
  }

  @Override
  public ResponseEntity<Void> delete(UUID userId) {
    userService.delete(userId);
    return ResponseEntity.noContent().build();
  }

  private BinaryContentCreateRequest resolveProfileRequest(MultipartFile profileFile) {
    if (profileFile == null || profileFile.isEmpty()) {
      return null;
    }
    try {
      return new BinaryContentCreateRequest(
          profileFile.getBytes(), profileFile.getOriginalFilename(), profileFile.getContentType(),
          profileFile.getSize()
      );
    } catch (IOException e) {
      throw new RuntimeException("이미지 처리 중 오류 발생", e);
    }
  }
}