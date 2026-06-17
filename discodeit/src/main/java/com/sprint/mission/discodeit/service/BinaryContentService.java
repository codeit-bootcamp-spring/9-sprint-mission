package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface BinaryContentService {

  BinaryContentDto create(BinaryContentCreateRequest request);

  BinaryContentDto find(UUID binaryContentId);

  List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds);

  void delete(UUID binaryContentId);

  @Transactional(propagation = Propagation.REQUIRES_NEW) // 🌟 매우 중요: 커밋 후 실행될 새로운 트랜잭션을 염니다.
  void updateStatus(UUID binaryContentId, BinaryContentStatus status);
}
