package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.CreateUserDto;
import com.sprint.mission.discodeit.dto.UpdateUserDto;
import com.sprint.mission.discodeit.dto.UserFinder;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    UUID create(CreateUserDto dto);

    boolean update(UpdateUserDto requestDto);

    UserFinder find(String name);

    List<UserFinder> findAll();

    boolean delete(UUID id);

    UUID userNameToId(String name);

    String userIdToName(UUID id);

    boolean checkInvalid(UUID id, String pw);

    boolean duplicateChecker(String checkThis, String findThis);
}
