package com.sprint.mission.discordit.service;

import com.sprint.mission.discordit.dto.data.UserDto;
import com.sprint.mission.discordit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discordit.dto.request.UserCreateRequest;
import com.sprint.mission.discordit.dto.request.UserUpdateRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

  UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> profileCreateRequest);

  UserDto find(UUID userId);

  List<UserDto> findAll();

  UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> profileCreateRequest);

  void delete(UUID userId);
}
