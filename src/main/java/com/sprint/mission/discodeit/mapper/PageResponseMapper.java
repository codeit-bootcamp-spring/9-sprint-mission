package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
public class PageResponseMapper {

  public <T> PageResponse<T> fromSlice(Slice<T> slice) {

    String nextCursor = null;

    if (!slice.getContent().isEmpty() && slice.hasNext()) {

      T last = slice.getContent().get(slice.getContent().size() - 1);

      if (last instanceof MessageDto messageDto) {
        nextCursor = messageDto.createdAt().toString() + "|" + messageDto.id().toString();
      }
    }

    return PageResponse.of(
        slice.getContent(),
        nextCursor,
        slice.getSize(),
        slice.hasNext(),
        (long) slice.getNumberOfElements()
    );
  }
}