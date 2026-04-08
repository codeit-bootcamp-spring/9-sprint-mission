package com.sprint.mission.discodeit.dto.response;

import java.util.List;

/**
 * 커서 기반 페이지네이션 응답 DTO.
 * nextCursor가 null이면 마지막 페이지이다.
 */
public record PageResponse<T>(
    List<T> content,
    String nextCursor,
    int size,
    boolean hasNext,
    Long totalElements
) {

}
