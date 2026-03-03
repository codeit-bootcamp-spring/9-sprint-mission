package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import java.util.UUID;

/**
 * 바이너리 콘텐츠 서비스 인터페이스
 * 파일 업로드, 조회, 삭제 등의 기능을 정의합니다.
 */
public interface BinaryContentService {
    /**
     * 바이너리 콘텐츠를 생성합니다.
     *
     * @param request 바이너리 콘텐츠 생성 요청
     * @return 생성된 바이너리 콘텐츠
     */
    BinaryContent create(BinaryContentCreateRequest request);

    /**
     * ID로 바이너리 콘텐츠를 조회합니다.
     *
     * @param id 바이너리 콘텐츠 ID
     * @return 조회된 바이너리 콘텐츠
     */
    BinaryContent find(UUID id);

    /**
     * 여러 ID로 바이너리 콘텐츠 목록을 조회합니다.
     *
     * @param ids 바이너리 콘텐츠 ID 목록
     * @return 조회된 바이너리 콘텐츠 목록
     */
    List<BinaryContent> findAllByIdIn(List<UUID> ids);

    /**
     * ID로 바이너리 콘텐츠를 삭제합니다.
     *
     * @param id 삭제할 바이너리 콘텐츠 ID
     */
    void delete(UUID id);
}