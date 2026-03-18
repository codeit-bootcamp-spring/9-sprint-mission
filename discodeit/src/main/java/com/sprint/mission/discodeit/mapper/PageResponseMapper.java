package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

//스프링이 주는 Slice나 page객체를 PageResponse봉투에 담아주는 역할

@Component
public class PageResponseMapper {

  //Slice를 PageResponse로 변환하는 메서드
  public <T> PageResponse<T> fromSlice(Slice<T> slice) {
    return new PageResponse<>(
        slice.getContent(),
        slice.getNumber(),
        slice.getSize(),
        slice.hasNext(),
        null //전체 개수를 모르니까 null
    );
  }

  public <T> PageResponse<T> fromPage(Page<T> page) {
    return new PageResponse<>(
        page.getContent(),
        page.getNumber(),
        page.getSize(),
        page.hasNext(),
        page.getTotalElements() //Page는 전체 개수 알고 있음!
    );
  }

}
