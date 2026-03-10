package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
public class PageResponseMapper {

  public <T> PageResponse<T> from(Slice<T> slice, Function<T, Object> cursorExtractor) {
    List<T> content = slice.getContent();
    Object nextCursor = null;

    if (slice.hasNext() && !content.isEmpty()) {
      T lastItem = content.get(content.size() - 1);
      nextCursor = cursorExtractor.apply(lastItem);
    }

    return new PageResponse<>(
        content,
        nextCursor,
        slice.getSize(),
        slice.hasNext(),
        null
    );
  }
}