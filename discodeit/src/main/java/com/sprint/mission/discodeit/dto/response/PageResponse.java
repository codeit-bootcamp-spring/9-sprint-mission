package com.sprint.mission.discodeit.dto.response;

import lombok.Getter;
import java.util.List;

@Getter
public class PageResponse<T> {

  private final List<T> content;
  private final Object nextCursor;
  private final int size;
  private final boolean hasNext;
  private final Long totalElements;

  public PageResponse(List<T> content, Object nextCursor, int size, boolean hasNext,
      Long totalElements) {
    this.content = content;
    this.nextCursor = nextCursor;
    this.size = size;
    this.hasNext = hasNext;
    this.totalElements = totalElements;
  }

  public static <T> PageResponse<T> of(List<T> content, Object nextCursor, int size,
      boolean hasNext) {
    return new PageResponse<>(content, nextCursor, size, hasNext, null);
  }

  public static <T> PageResponse<T> of(List<T> content, Object nextCursor, int size,
      boolean hasNext, Long totalElements) {
    return new PageResponse<>(content, nextCursor, size, hasNext, totalElements);
  }
}