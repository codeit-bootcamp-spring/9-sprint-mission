package com.sprint.mission.discodeit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

  private List<T> content;
  private String nextCursor;
  private int size;
  private boolean hasNext;
  private long totalElements;

  public static <T> PageResponse<T> of(
      List<T> content,
      String nextCursor,
      int size,
      boolean hasNext,
      long totalElements
  ) {
    return new PageResponse<>(content, nextCursor, size, hasNext, totalElements);
  }
}