package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper; // ✅ 추가

  public UserDto toDto(User user, boolean online) {
    BinaryContentDto profileDto = null;

    if (user.getProfileId() != null) {
      profileDto = binaryContentRepository.findById(user.getProfileId())
          .map(binaryContentMapper::toDto)
          .orElse(null);
    }

    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        profileDto,
        online
    );
  }
}