package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.RequestCreateUserDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateUserDto;
import com.sprint.mission.discodeit.dto.response.ResponseUserDto;
import java.util.List;
import java.util.UUID;

public interface UserService {

  UUID usernameToId(String name);

  boolean isPresent(UUID id);

  boolean isInvalid(UUID userId, String password);

  ResponseUserDto create(RequestCreateUserDto requestDto, UUID profileId);

  ResponseUserDto update(UUID userId, RequestUpdateUserDto requestDto, UUID profileId);

  ResponseUserDto find(String name);

  ResponseUserDto find(UUID id);

  List<ResponseUserDto> findAll();

  boolean delete(UUID id);
}