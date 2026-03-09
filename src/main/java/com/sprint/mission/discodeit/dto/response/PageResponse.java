package com.sprint.mission.discodeit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageResponse<T> {

  private List<T> content;
  private int number;
  private int size;
  private boolean hasNext;
  private Long totalElements;
}