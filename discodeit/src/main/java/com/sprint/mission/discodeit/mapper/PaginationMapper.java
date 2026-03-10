package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PaginationMapper {

    // 커서 기반 (Message 등)
    public <T> PageResponse<T> toDto(Slice<T> slice, Function<T, Object> cursorExtractor) {
        Object nextCursor = null;
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

    // 오프셋 기반 (User, Channel 등)
    public <T> PageResponse<T> toDto(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.hasNext() ? page.getNumber() + 1 : null, // 다음 페이지 번호를 커서 필드에 할당
                page.getSize(),
                page.hasNext(),
                page.getTotalElements()
        );
    }
}