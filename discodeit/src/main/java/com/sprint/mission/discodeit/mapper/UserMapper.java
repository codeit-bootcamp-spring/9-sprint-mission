package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Context;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // 접속 여부 필요 없는 작업 시 사용
    @Named("toDtoBasic")
    @Mapping(target = "online", constant = "false")
    UserDto toDto(User user);

    @IterableMapping(qualifiedByName = "toDtoBasic")
    List<UserDto> toDtoList(List<User> users);

    @Named("toDtoWithOnline")
    @Mapping(target = "online", expression = "java(onlineUserIds != null && onlineUserIds.contains(user.getId()))")
    UserDto toDto(User user, @Context Set<UUID> onlineUserIds);

    @IterableMapping(qualifiedByName = "toDtoWithOnline")
    List<UserDto> toDtoList(List<User> users, @Context Set<UUID> onlineUserIds);
}

