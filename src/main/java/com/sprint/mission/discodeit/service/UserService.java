package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserDto create(UserCreateRequest request, MultipartFile profile);

    UserDto findById(UUID userId);

    List<UserDto> findAll();

    UserDto update(UUID userId, UserUpdateRequest request, MultipartFile profile);

    void delete(UUID userId);
}