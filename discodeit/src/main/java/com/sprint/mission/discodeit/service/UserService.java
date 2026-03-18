package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;

import java.util.*;
import org.springframework.data.jpa.repository.Query;

public interface UserService {
    UserDto create(UserCreateRequest request, Optional<BinaryContentCreateRequest> profile);

    UserDto find(UUID userId);

    List<UserDto> findAll();

    UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
        Optional<BinaryContentCreateRequest> profileCreateRequest);

    void delete(UUID userId);
}
