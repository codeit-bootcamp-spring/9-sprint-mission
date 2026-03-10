package com.sprint.mission.discodeit.dto.response;

import java.util.List;

public record PageResponse<T>(
    List<T> content,
    Object nextCursor,   // 다음 페이지를 위한 포인터 (v1.2 핵심)
    int size,
    boolean hasNext,
    Long totalElements   // 필요한 경우에만 사용 (Slice에서는 보통 null)
) {

}