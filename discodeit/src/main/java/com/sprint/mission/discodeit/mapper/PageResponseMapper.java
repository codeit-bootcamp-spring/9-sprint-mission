package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;


@Component
public class PageResponseMapper {


  public <T, CURSOR> PageResponse<T> fromSlice(Slice<T> slice, Function<T, CURSOR> idExtractor) {
    List<T> content = slice.getContent();

    CURSOR nextCursor = (slice.hasNext() && !content.isEmpty())
        ? idExtractor.apply(content.get(content.size() - 1))
        : null;

    return new PageResponse<>(
        content,
        nextCursor,
        slice.getSize(),
        slice.hasNext(),
        0L
    );
  }
}