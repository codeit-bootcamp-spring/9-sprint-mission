package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.BinaryContentException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final ApplicationEventPublisher eventPublisher;  // 이벤트 발행자 추가!

  @Transactional
  @Override
  public BinaryContentDto create(BinaryContentCreateRequest request) {
    log.info("파일 업로드(생성) 로직 시작 - 파일명: {}, 크기: {} bytes", request.fileName(),
        request.bytes().length);

    BinaryContent binaryContent = new BinaryContent(
        request.fileName(),
        (long) request.bytes().length,
        request.contentType()
    );

    BinaryContent savedEntity = binaryContentRepository.save(binaryContent);

    // storage.put() 대신 이벤트 발행
    eventPublisher.publishEvent(
        new BinaryContentCreatedEvent(savedEntity.getId(), request.bytes())
    );

    log.info("파일 메타데이터 DB 저장 완료, 스토리지 저장 이벤트 발행 - 파일 ID: {}", savedEntity.getId());
    return binaryContentMapper.toDto(savedEntity);
  }

  @Transactional
  @Override
  public BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus status) {
    log.info("파일 상태 업데이트 시작 - 파일 ID: {}, 새 상태: {}", binaryContentId, status);
    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new BinaryContentException(
            ErrorCode.BINARY_CONTENT_NOT_FOUND, Map.of("binaryContentId", binaryContentId)
        ));
    binaryContent.updateStatus(status);
    log.info("파일 상태 업데이트 완료 - 파일 ID: {}, 상태: {}", binaryContentId, status);
    return binaryContentMapper.toDto(binaryContent);
  }

  @Override
  public BinaryContentDto find(UUID binaryContentId) {
    log.info("파일 단건 조회 로직 시작 - 대상 파일 ID: {}", binaryContentId);
    return binaryContentRepository.findById(binaryContentId)
        .map(binaryContentMapper::toDto)
        .orElseThrow(() -> {
          log.warn("파일 조회 실패: 존재하지 않는 파일 ID 입니다. ({})", binaryContentId);
          return new BinaryContentException(
              ErrorCode.BINARY_CONTENT_NOT_FOUND, Map.of("binaryContentId", binaryContentId));
        });
  }

  @Override
  public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
    log.info("파일 다건 조회 로직 시작 - 요청 개수: {}개", binaryContentIds.size());
    return binaryContentRepository.findAllByIdIn(binaryContentIds).stream()
        .map(binaryContentMapper::toDto)
        .toList();
  }

  @Override
  public void delete(UUID binaryContentId) {
    log.info("파일 삭제 로직 시작 - 대상 파일 ID: {}", binaryContentId);
    if (!binaryContentRepository.existsById(binaryContentId)) {
      log.warn("파일 삭제 실패: 존재하지 않는 파일 ID 입니다. ({})", binaryContentId);
      throw new BinaryContentException(ErrorCode.BINARY_CONTENT_NOT_FOUND,
          Map.of("binaryContentId", binaryContentId));
    }
    binaryContentRepository.deleteById(binaryContentId);
    log.info("파일 삭제 완료 - 대상 파일 ID: {}", binaryContentId);
  }
}