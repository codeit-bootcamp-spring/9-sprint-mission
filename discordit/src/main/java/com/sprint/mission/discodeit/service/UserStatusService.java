package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.RequestCreateUserStatusDto;
import com.sprint.mission.discodeit.dto.request.RequestDeleteUserStatusDto;
import com.sprint.mission.discodeit.dto.request.RequestFindUserStatusDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateUserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;

public interface UserStatusService {

  void create(RequestCreateUserStatusDto requestDto);

  boolean find(RequestFindUserStatusDto requestDto);

  UserStatus update(RequestUpdateUserStatusDto requestDto);

  void delete(RequestDeleteUserStatusDto requestDto);
}
