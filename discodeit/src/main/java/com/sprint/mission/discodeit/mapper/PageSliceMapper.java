package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class PageSliceMapper {

  public <T, D> PageResponse<D> toPageResponse(
      Slice<T> slice,
      Function<T, D> mapper,
      Function<D, Object> nextCursorExtractor
  ) {
    List<D> content = slice.getContent().stream()
        .map(mapper)
        .toList();

    Object nextCursor = slice.hasNext() && !content.isEmpty()
        ? nextCursorExtractor.apply(content.get(content.size() - 1))
        : null;

    return new PageResponse<>(
        content,
        nextCursor,
        slice.getSize(),
        slice.hasNext(),
        0L
    );
  }

  public <T, D> PageResponse<D> toPageResponse(Page<T> page, Function<T, D> mapper) {
    List<D> content = page.getContent().stream()
        .map(mapper)
        .toList();

    return new PageResponse<>(
        content,
        page.getNumber(),
        page.getSize(),
        page.hasNext(),
        page.getTotalElements()
    );
  }

  public <T, D> List<D> toList(List<T> content, Function<T, D> mapper) {
    return content.stream()
        .map(mapper)
        .toList();
  }
}
