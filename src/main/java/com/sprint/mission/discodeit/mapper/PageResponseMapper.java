package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

/**
 * Slice 조회 결과를 커서 기반 PageResponse로 변환하는 매퍼.
 */
@Component
public class PageResponseMapper {

  /** Slice의 마지막 요소에서 커서 값을 추출하여 PageResponse를 생성한다. */
  public <T> PageResponse<T> fromSlice(Slice<T> slice, Function<T, String> cursorExtractor) {
    List<T> content = slice.getContent();
    String nextCursor = null;
    if (slice.hasNext() && !content.isEmpty()) {
      nextCursor = cursorExtractor.apply(content.get(content.size() - 1));
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
