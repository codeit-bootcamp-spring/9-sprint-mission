package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.domain.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
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
    log.debug("파일 업로드 요청: fileName={}, contentType={}, size={}bytes",
        fileName, contentType, bytes.length);

    BinaryContent binaryContent = new BinaryContent(
        fileName,
        (long) bytes.length,
        contentType
    );
    binaryContentRepository.save(binaryContent);
    binaryContentStorage.put(binaryContent.getId(), bytes);
    log.info("파일 업로드 완료: binaryContentId={}, fileName={}", binaryContent.getId(), fileName);

    return binaryContentMapper.toDto(binaryContent);
  }

  @Override
  public BinaryContentDto find(UUID binaryContentId) {
    log.debug("파일 다운로드 요청: binaryContentId={}", binaryContentId);
    return binaryContentRepository.findById(binaryContentId)
        .map(binaryContentMapper::toDto)
        .orElseThrow(() -> {
          log.warn("존재하지 않는 파일 조회: binaryContentId={}", binaryContentId);
          return new BinaryContentNotFoundException(binaryContentId);  // ← 교체
        });
  }

  @Override
  public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
    List<BinaryContentDto> contents = binaryContentRepository.findAllById(binaryContentIds)
        .stream()
        .map(binaryContentMapper::toDto)
        .toList();
    log.debug("파일 목록 조회: 요청 {}개 중 {}개 조회됨", binaryContentIds.size(), contents.size());
    return contents;
  }

  @Transactional
  @Override
  public void delete(UUID binaryContentId) {
    log.debug("파일 삭제 요청: binaryContentId={}", binaryContentId);
    if (!binaryContentRepository.existsById(binaryContentId)) {
      log.warn("존재하지 않는 파일 삭제 시도: binaryContentId={}", binaryContentId);
      throw new BinaryContentNotFoundException(binaryContentId);  // ← 교체
    }

    binaryContentRepository.deleteById(binaryContentId);
    log.info("파일 삭제 완료: binaryContentId={}", binaryContentId);
  }
}