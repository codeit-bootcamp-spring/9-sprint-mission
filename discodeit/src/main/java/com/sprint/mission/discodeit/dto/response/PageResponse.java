package com.sprint.mission.discodeit.dto.response;

//어떤 엔티티의 목록이든 공통된 형식으로 포장해 주는 마법의 봉투 !

import java.util.List;

public record PageResponse<T>(
    List<T> content, //실제 데이터들, 메세지목록...
    int number, //현재 페이지 번호, 0부터 시작
    int size, //한 페이지당 데이터 개수
    boolean hasNext, //다음 페이지가 있는지 여부
    Long totalElements //전체 데이터 개수
) {

}
