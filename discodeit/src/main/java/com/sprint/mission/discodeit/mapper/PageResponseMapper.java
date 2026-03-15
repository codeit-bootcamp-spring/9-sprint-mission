package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import java.util.function.Function;

@Component
public class PageResponseMapper {

  public <T, ID> PageResponse<T> fromSlice(Slice<T> slice, Function<T, ID> idExtractor) {
    ID nextCursor = (slice.hasNext() && !slice.getContent().isEmpty())
        ? idExtractor.apply(slice.getContent().get(slice.getContent().size() - 1))
        : null;

    return new PageResponse<>(
        slice.getContent(),
        nextCursor,
        slice.getSize(),
        slice.hasNext(),
        null
    );
  }
}