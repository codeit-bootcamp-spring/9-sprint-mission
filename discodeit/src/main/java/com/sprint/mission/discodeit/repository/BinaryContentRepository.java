package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 바이너리 콘텐츠 저장소 인터페이스
 * 바이너리 파일 데이터에 대한 CRUD 작업을 정의합니다.
 */
public interface BinaryContentRepository {
    /**
     * 바이너리 콘텐츠를 저장합니다.
     *
     * @param binaryContent 저장할 바이너리 콘텐츠
     * @return 저장된 바이너리 콘텐츠
     */
    BinaryContent save(BinaryContent binaryContent);

    /**
     * ID로 바이너리 콘텐츠를 조회합니다.
     *
     * @param id 바이너리 콘텐츠 ID
     * @return 조회된 바이너리 콘텐츠 (Optional)
     */
    Optional<BinaryContent> findById(UUID id);

    /**
     * 모든 바이너리 콘텐츠를 조회합니다.
     *
     * @return 모든 바이너리 콘텐츠 목록
     */
    List<BinaryContent> findAll();

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
    void deleteById(UUID id);

    /**
     * ID로 바이너리 콘텐츠의 존재 여부를 확인합니다.
     *
     * @param id 바이너리 콘텐츠 ID
     * @return 존재 여부
     */
    boolean existsById(UUID id);
}