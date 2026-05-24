package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
public class PageResponseMapper {

  public <T> PageResponse<T> fromSlice(Slice<T> slice, Object nextCursor){
    return new PageResponse<>(
        slice.getContent(),
        nextCursor,
        slice.getSize(),
        slice.hasNext(),
        slice.stream().count()
    );
  }

  public <T> PageResponse<T> fromPage(Page<T> page){
    List<T> content = page.getContent();
    Instant nextCursor = content.isEmpty() ? null :
        ((MessageDto) content.get(content.size() - 1)).createdAt();
    return new PageResponse<>(
        page.getContent(),
        nextCursor,
        page.getSize(),
        page.hasNext(),
        page.stream().count()
    );
  }
}
