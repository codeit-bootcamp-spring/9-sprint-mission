package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.RequestCreateUserStatusDto;
import com.sprint.mission.discodeit.dto.request.RequestDeleteUserStatusDto;
import com.sprint.mission.discodeit.dto.request.RequestFindUserStatusDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateUserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exepction.DoNotDuplicate;
import com.sprint.mission.discodeit.exepction.global.NotFound;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;

  @Override
  public void create(RequestCreateUserStatusDto requestDto) {
    RequestFindUserStatusDto dto = new RequestFindUserStatusDto(requestDto.id(), requestDto.name());

    if (userRepository.userIdToName(requestDto.id()).isEmpty()) {
      throw new NotFound("해당 유저가 없습니다.");
    }
    if (userStatusRepository.find(dto) != null) {
      throw new DoNotDuplicate("이미 존재합니다.");
    }

    userStatusRepository.create(requestDto);
  }

  @Override
  public boolean find(RequestFindUserStatusDto requestDto) {
    return userStatusRepository.find(requestDto);
  }

  public void findAll() {
  }

  @Override
  public UserStatus update(RequestUpdateUserStatusDto requestDto) {
    return userStatusRepository.update(requestDto);
  }

  @Override
  public void delete(RequestDeleteUserStatusDto requestDto) {
    userStatusRepository.delete(requestDto);
  }
}
