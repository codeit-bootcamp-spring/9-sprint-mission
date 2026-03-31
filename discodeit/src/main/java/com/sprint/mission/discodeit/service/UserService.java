package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import java.util.List;
import java.util.UUID;

public interface UserService {

  UserDto create(UserCreateRequest request, BinaryContentCreateRequest profileRequest);

  UserDto update(UUID id, UserUpdateRequest request, BinaryContentCreateRequest profileRequest);

  void delete(UUID id);

  List<UserDto> findAll();

  UserDto findById(UUID id);
}