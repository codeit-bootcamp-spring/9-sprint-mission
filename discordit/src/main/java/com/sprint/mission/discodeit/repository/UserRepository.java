package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.request.RequestCreateUserDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateUserDto;
import com.sprint.mission.discodeit.dto.response.ResponseUserDto;
import java.util.List;
import java.util.UUID;

public interface UserRepository {

  ResponseUserDto create(RequestCreateUserDto dto, UUID profileId);

  ResponseUserDto update(UUID userId, RequestUpdateUserDto requestDto, UUID profileId);

  ResponseUserDto find(String name);

  ResponseUserDto find(UUID userId);

  List<ResponseUserDto> findAll();

  boolean delete(UUID id);

  boolean isPresent(UUID id);

  UUID usernameToId(String name);

  String userIdToName(UUID id);

  boolean checkInvalid(UUID id, String pw);

  void duplicateChecker(String checkThis, String findThis);
}
