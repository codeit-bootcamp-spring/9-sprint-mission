package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    UserDto create(UserCreateRequest userCreateRequest,
                   Optional<BinaryContentCreateRequest> profileCreateRequest);

    UserDto find(UUID userId);

    // List 반환을 PageResponse로 변경하고, page와 size 파라미터를 추가했습니다.
    PageResponse<UserDto> findAll(int page, int size);

    UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
                   Optional<BinaryContentCreateRequest> profileCreateRequest);

    void delete(UUID userId);
}