package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {
  // [수정] 불필요한 findAllByIdIn 삭제. 기본 제공되는 findAllById(Iterable<ID> ids)를 사용하세요.
}