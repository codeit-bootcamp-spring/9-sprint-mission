package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import java.util.List;

// User 및 BinaryContent 매퍼를 참조하여 작성자와 첨부파일 변환
@Mapper(componentModel = "spring", uses = {UserMapper.class, BinaryContentMapper.class})
public interface MessageMapper {

  MessageDto toDto(Message message);

  List<MessageDto> toDtoList(List<Message> messages);
}