package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import java.util.function.Function;

@Component
public class PageResponseMapper {

  /**
   * Slice를 Cursor 기반의 PageResponse로 변환합니다. [cite: 2026-03-09]
   *
   * @param slice       DB에서 가져온 데이터 조각
   * @param idExtractor 객체에서 ID(Cursor값)를 뽑아내는 함수 (예: MessageDto::id)
   */
  public <T, ID> PageResponse<T> fromSlice(Slice<T> slice, Function<T, ID> idExtractor) {
    // 다음 페이지가 있다면, 현재 리스트의 마지막 요소의 ID를 커서로 지정합니다. [cite: 2026-03-09]
    ID nextCursor = (slice.hasNext() && !slice.getContent().isEmpty())
        ? idExtractor.apply(slice.getContent().get(slice.getContent().size() - 1))
        : null;

    return new PageResponse<>(
        slice.getContent(),
        nextCursor,
        slice.getSize(),
        slice.hasNext(),
        null // 커서 페이징은 전체 개수를 계산하지 않아 성능이 빠릅니다. [cite: 2026-03-09]
    );
  }
}