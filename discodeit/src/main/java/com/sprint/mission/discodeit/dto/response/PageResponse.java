package com.sprint.mission.discodeit.dto.response;

import org.springframework.data.domain.Page;
import java.util.List;

// 제네릭 <T>를 사용하여 어떤 타입의 DTO든 담을 수 있는 범용적인 응답 레코드를 생성합니다.
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    // Spring Data JPA의 Page 객체를 입력받아 PageResponse로 변환하는 정적 팩토리 메서드입니다.
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(), // 0-based page index
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}