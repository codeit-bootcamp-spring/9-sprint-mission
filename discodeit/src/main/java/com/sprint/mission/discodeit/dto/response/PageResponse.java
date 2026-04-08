package com.sprint.mission.discodeit.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageResponse<T> {

  private List<T> content;      // 데이터 목록
  private int number;           // 현재 페이지 번호
  private int size;             // 페이지 크기
  private boolean hasNext;      // 다음 페이지 존재 여부
  private Long totalElements;   // 전체 개수 (null 가능)
}