package com.sprint.mission.discodeit.dto.response;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int number,
        int size,
        Long totalElements // Slice 사용 시 null이 들어갈 수 있도록 원시 타입(long)에서 래퍼 클래스(Long)로 변경
) {
}