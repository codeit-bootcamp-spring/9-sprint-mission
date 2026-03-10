package com.sprint.mission.discodeit.dto.data;

import java.util.List;

public record PageResponse<T>(
    List<T> content,
    int number,           // 현재 페이지 번호
    int size,             // 페이지 크기 (50)
    boolean hasNext,      // 다음 페이지 존재 여부
    Long totalElements    // 총 메시지 수 (null 가능)
) {}