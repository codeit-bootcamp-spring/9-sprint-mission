package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User create(UserCreateRequest request, MultipartFile profile);

    User findById(UUID userId);

    List<UserDto> findAll();

    User update(UUID userId, UserUpdateRequest request, MultipartFile profile);

    void delete(UUID userId);
}