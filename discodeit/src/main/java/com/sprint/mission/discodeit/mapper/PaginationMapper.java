package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PaginationMapper {

    // 1. 커서 기반 페이지네이션용 (Slice)
    public <T> PageResponse<T> toDto(Slice<T> slice, Function<T, Object> cursorExtractor) {
        Object nextCursor = null;
        // 다음 페이지가 있고 데이터가 존재한다면, 마지막 요소의 ID를 차기 커서로 지정
        if (slice.hasNext() && !slice.getContent().isEmpty()) {
            T lastItem = slice.getContent().get(slice.getContent().size() - 1);
            nextCursor = cursorExtractor.apply(lastItem);
        }

        return new PageResponse<>(
                slice.getContent(),
                nextCursor,
                slice.getSize(),
                slice.hasNext(),
                null
        );
    }

    // 2. 기존 오프셋 기반 페이지네이션용 (Page - User/Channel 등에서 사용)
    public <T> PageResponse<T> toDto(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.hasNext() ? page.getNumber() + 1 : null, // 다음 페이지 번호를 커서처럼 활용
                page.getSize(),
                page.hasNext(),
                page.getTotalElements()
        );
    }
}