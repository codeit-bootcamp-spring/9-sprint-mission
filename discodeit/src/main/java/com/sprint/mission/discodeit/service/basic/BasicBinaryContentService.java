package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Transactional
  @Override
  public BinaryContentDto create(BinaryContentCreateRequest request) {
    String fileName = request.fileName();
    byte[] bytes = request.bytes();
    String contentType = request.contentType();

    log.debug("바이너리 콘텐츠 생성 시도, 파일명={}, 크기={}바이트, 타입={}", fileName, bytes.length, contentType);

    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType);
    binaryContentRepository.save(binaryContent);
    binaryContentStorage.put(binaryContent.getId(), bytes);

    log.info("바이너리 콘텐츠 생성 완료, id={}, 파일명={}", binaryContent.getId(), fileName);

    return binaryContentMapper.toDto(binaryContent);
  }

  @Override
  public BinaryContentDto find(UUID binaryContentId) {
    log.debug("바이너리 콘텐츠 조회 시도, id={}", binaryContentId);

    return binaryContentRepository.findById(binaryContentId)
        .map(binaryContentMapper::toDto)
        .orElseThrow(() -> {
          log.warn("바이너리 콘텐츠를 찾을 수 없습니다, id={}", binaryContentId);
          return new BinaryContentNotFoundException(binaryContentId);
        });
  }

  @Override
  public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
    log.debug("바이너리 콘텐츠 다중 조회 시도, id 개수={}", binaryContentIds.size());

    List<BinaryContentDto> result = binaryContentRepository.findAllById(binaryContentIds).stream()
        .map(binaryContentMapper::toDto)
        .toList();

    log.info("바이너리 콘텐츠 다중 조회 완료, 조회된 개수={}", result.size());
    return result;
  }

  @Transactional
  @Override
  public void delete(UUID binaryContentId) {
    log.debug("바이너리 콘텐츠 삭제 시도, id={}", binaryContentId);

    if (!binaryContentRepository.existsById(binaryContentId)) {
      log.error("삭제할 바이너리 콘텐츠를 찾을 수 없습니다, id={}", binaryContentId);
      throw new BinaryContentNotFoundException(binaryContentId);
    }

    binaryContentRepository.deleteById(binaryContentId);
    log.info("바이너리 콘텐츠 삭제 완료, id={}", binaryContentId);
  }
}