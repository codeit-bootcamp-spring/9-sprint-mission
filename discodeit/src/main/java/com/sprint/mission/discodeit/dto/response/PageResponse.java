package com.sprint.mission.discodeit.dto.response;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        Object nextCursor, // 다음 요청 시 사용할 커서 값
        int size,
        boolean hasNext,   // 다음 페이지 존재 여부
        Long totalElements // 전체 개수 (Slice 사용 시 null 가능)
) {
}