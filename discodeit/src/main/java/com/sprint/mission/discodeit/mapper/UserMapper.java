package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.JwtRegistry;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public abstract class UserMapper {

  @Autowired
  private JwtRegistry jwtRegistry;

  @Mapping(target = "profile", source = "profile")
  @Mapping(target = "online", source = "id", qualifiedByName = "mapOnline")
  public abstract UserResponse toResponse(User user);

  @Named("mapOnline")
  protected Boolean mapOnline(UUID userId) {
    return jwtRegistry.hasActiveJwtInformationByUserId(userId);
  }
}
