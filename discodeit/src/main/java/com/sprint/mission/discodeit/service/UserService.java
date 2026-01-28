package com.sprint.mission.discodeit.service; // 이 경로가 구현체의 import문과 일치해야 합니다.

import com.sprint.mission.discodeit.dto.UserDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<UserDto.Response> create(UserDto.CreateRequest request);
    Optional<UserDto.Response> findById(UUID id);
    List<UserDto.Response> findAll();
    Optional<UserDto.Response> update(UUID id, UserDto.UpdateRequest request);
    boolean delete(UUID id);
}