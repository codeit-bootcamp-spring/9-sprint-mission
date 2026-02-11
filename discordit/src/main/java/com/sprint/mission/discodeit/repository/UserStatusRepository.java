package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.CreateUserStatusDto;
import com.sprint.mission.discodeit.dto.DeleteUserStatusDto;
import com.sprint.mission.discodeit.dto.FindUserStatusDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateDto;

import java.util.List;

public interface UserStatusRepository {
    boolean create(CreateUserStatusDto requestDto);

    String find(FindUserStatusDto requestDto);

    List<String> findAll(List<FindUserStatusDto> requestDto);

    boolean update(UserStatusUpdateDto requestDto);

    boolean delete(DeleteUserStatusDto requestDto);
}
