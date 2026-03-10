package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

// UserMapper를 참조하여 참여자(User -> UserDto) 변환 로직을 가져옵니다.
@Mapper(
    componentModel = "spring",
    uses = {UserMapper.class},
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS // null 체크를 엄격하게 수행
)
public interface ChannelMapper {

  /**
   * [핵심 수정] 1. participants -> members 매핑 시, 원본이 null이면 빈 리스트를 반환하도록 안전장치 가동 2. name이나 description이
   * null이면 프론트엔드 .length 에러 방지를 위해 기본값 매핑
   */
  @Mapping(target = "members", source = "participants")
  @Mapping(target = "name", source = "name", defaultValue = "이름 없는 채널")
  @Mapping(target = "description", source = "description", defaultValue = "")
  ChannelDto toDto(Channel channel);

  List<ChannelDto> toDtoList(List<Channel> channels);
}