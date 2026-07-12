package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.entity.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.procedure.internal.ProcedureParamBindings;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper mapper;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  @Transactional
  public BinaryContentDto create(BinaryContentCreateRequest request) {
    String fileName = request.fileName();
    byte[] bytes = request.bytes();
    String contentType = request.contentType();
    BinaryContent binaryContent = BinaryContent.builder().contentType(contentType)
        .fileName(fileName).size((long) bytes.length).build();
    binaryContentRepository.save(binaryContent);
    eventPublisher.publishEvent(new BinaryContentCreatedEvent(binaryContent.getId(), bytes));
    return mapper.toDto(binaryContent);

  }

  @Override
  public BinaryContentDto find(UUID binaryContentId) {
    return mapper.toDto(binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new NoSuchElementException(
            "BinaryContent with id " + binaryContentId + " not found")));
  }

  @Override
  public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
    return binaryContentRepository.findAllByIdIn(binaryContentIds).stream()
        .map(mapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public void delete(UUID binaryContentId) {
    if (!binaryContentRepository.existsById(binaryContentId)) {
      throw new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found");
    }
    binaryContentRepository.deleteById(binaryContentId);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus status) {
    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new EntityNotFoundException("BinaryContent가 존재하지 않습니다."));
    binaryContent.updateStatus(status);
    log.info("파일 ID: {}, 상태 업데이트: {}", binaryContentId, status);

    return mapper.toDto(binaryContent);

  }
}
