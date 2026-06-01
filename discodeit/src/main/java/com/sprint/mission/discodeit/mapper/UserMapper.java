package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

  private final BinaryContentMapper binaryContentMapper;
  // SessionRegistry 삭제

  public UserDto toDto(User user) {
    // STATELESS로 전환 후 세션 기반 온라인 체크 불가 → false로 고정
    boolean online = false;

    BinaryContentDto profile = Optional.ofNullable(user.getProfile())
        .map(binaryContentMapper::toDto)
        .orElse(null);

    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        profile,
        online,
        user.getRole()
    );
  }
}