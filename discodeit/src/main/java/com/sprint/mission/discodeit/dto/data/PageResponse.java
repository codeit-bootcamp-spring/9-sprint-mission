package com.sprint.mission.discodeit.dto.data;

import java.util.List;

public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    boolean hasNext,
    long totalElements,
    int totalPages
) {
}
