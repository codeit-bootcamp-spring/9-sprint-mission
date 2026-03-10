package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// Profile 매핑을 위해 BinaryContentMapper를 사용하도록 지정
@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {

    // User 엔티티의 status 객체 안에 있는 online 값을 UserDto의 online으로 매핑
    @Mapping(target = "online", source = "status.online")
    UserDto toDto(User user);
}