package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

  Optional<User> create(UserCreateRequest request,
      Optional<BinaryContentCreateRequest> profileRequest);

  Optional<User> update(UUID id, UserUpdateRequest request,
      Optional<BinaryContentCreateRequest> profileRequest);

  boolean delete(UUID id);

  List<User> findAll();

  Optional<User> findById(UUID id);
}