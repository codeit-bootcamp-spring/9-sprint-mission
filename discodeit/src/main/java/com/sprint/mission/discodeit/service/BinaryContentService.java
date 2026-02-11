package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContentResponse;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    // 1. 생성
    BinaryContentResponse create(BinaryContentCreateRequest request);

    // 2. 단건 조회
    BinaryContentResponse find(UUID binaryContentId);

    // 3. 목록 조회 (ID 리스트로 여러 개 조회)
    List<BinaryContentResponse> findAllByIdIn(List<UUID> ids);

    // 4. 삭제
    void delete(UUID binaryContentId);
}