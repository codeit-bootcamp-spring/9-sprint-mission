package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.api.UserDto;
import com.sprint.mission.discodeit.dto.user.ProfileImageParams;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDeleteRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserView;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

  private final UserService userService;

  private UserDto toDto(UserView v) {
    return new UserDto(
        v.id(),
        v.createdAt(),
        v.updatedAt(),
        v.username(),
        v.email(),
        v.profileImageId(),
        v.online()
    );
  }

  @Override
  public ResponseEntity<UserView> create(
      @RequestPart("userCreateRequest") UserCreateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    if (profile == null || profile.isEmpty()) {
      return ResponseEntity.status(201).body(userService.create(request));
    }
    byte[] bytes;
    try {
      bytes = profile.getBytes();
    } catch (java.io.IOException e) {
      throw new RuntimeException("Failed to read profile file", e);
    }
    var profileImage = new ProfileImageParams(
        bytes,
        profile.getContentType(),
        profile.getOriginalFilename()
    );
    var merged = new UserCreateRequest(request.user(), profileImage);
    return ResponseEntity.status(201).body(userService.create(merged));
  }

  @Override
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserView> users = userService.findAll();
    List<UserDto> result = users.stream()
        .map(this::toDto)
        .toList();
    return ResponseEntity.ok(result);
  }

  @Override
  public UserView findByUsername(String username) {
    return userService.findByUsername(username);
  }

  @Override
  public UserView findById(UUID userId) {
    return userService.findById(userId);
  }

  @Override
  public ResponseEntity<UserView> update(
      UUID userId,
      UserUpdateRequest.Params params,
      MultipartFile profile
  ) {
    if (profile == null || profile.isEmpty()) {
      return ResponseEntity.ok(
          userService.update(new UserUpdateRequest(userId, params))
      );
    }
    byte[] bytes;
    try {
      bytes = profile.getBytes();
    } catch (java.io.IOException e) {
      throw new RuntimeException("Failed to read profile file", e);
    }

    var profileImage = new ProfileImageParams(
        bytes,
        profile.getContentType(),
        profile.getOriginalFilename()
    );

    var mergedParams = new UserUpdateRequest.Params(
        params.user(),
        profileImage
    );

    return ResponseEntity.ok(
        userService.update(new UserUpdateRequest(userId, mergedParams))
    );
  }

  @Override
  public ResponseEntity<Void> delete(UUID userId) {
    userService.delete(new UserDeleteRequest(userId));
    return ResponseEntity.noContent().build();
  }

}