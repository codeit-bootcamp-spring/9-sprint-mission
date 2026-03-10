package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
public class PaginationMapper {

    // 1. 총 개수가 필요 없는 Slice용 매핑 (totalElements = null)
    public <T> PageResponse<T> toDto(Slice<T> slice) {
        return new PageResponse<>(
                slice.getContent(),
                slice.getNumber(),
                slice.getSize(),
                null // 총 메시지가 몇 개인지 알 필요 없으므로 null 반환
        );
    }

    // 2. 총 개수가 필요한 일반 Page용 매핑
    public <T> PageResponse<T> toDto(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }
}