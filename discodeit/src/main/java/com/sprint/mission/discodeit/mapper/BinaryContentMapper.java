package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

@Component //스프링한테 이 파일이 데이터를 변환해주는 도구라고 알려주기
public class BinaryContentMapper {

  public BinaryContentDto toDto(BinaryContent binaryContent) {
    //혹시라도 데이터가 비었으면(null), 에러 방지를 위해 null을 돌려줌
    if (binaryContent == null) {
      return null;
    }

    return new BinaryContentDto(
        binaryContent.getId(),
        binaryContent.getFileName(),
        binaryContent.getSize(),
        binaryContent.getContentType(),
        binaryContent.getCreatedAt(),
        null
    );
  }

}
