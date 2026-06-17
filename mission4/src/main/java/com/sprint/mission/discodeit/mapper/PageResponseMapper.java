package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

public class PageResponseMapper<T> {

  public static <T> PageResponse<T> fromSlice(Slice<T> slice, Function<T, Object> cursorExtract) {
    List<T> content = slice.getContent();
    Object nextCursor = null;

    if (content != null && !content.isEmpty()) {
      T lastItem = content.get(content.size() - 1);
      nextCursor = cursorExtract.apply(lastItem);
    }
    return new PageResponse<T>(
        slice.getContent(),
        nextCursor,
        slice.getSize(),
        slice.hasNext(),
        null

    );
  }

  public static <T> PageResponse<T> fromPage(Page<T> page) {
    return new PageResponse<T>(
        page.getContent(),
        page.getNumber(),
        page.getSize(),
        page.hasNext(),
        page.getTotalElements()

    );
  }

}
