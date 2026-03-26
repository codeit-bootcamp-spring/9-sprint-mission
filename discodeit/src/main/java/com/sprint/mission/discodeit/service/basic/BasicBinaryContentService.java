package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

@Slf4j
@Transactional(readOnly = true)
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
    log.debug("Creating binary content: fileName={}, contentType={}, size={} bytes",
        fileName, contentType, bytes.length);

    BinaryContent binaryContent = new BinaryContent(
        fileName,
        (long) bytes.length,
        contentType
    );

    BinaryContent createdBinaryContent = binaryContentRepository.save(binaryContent);

    try {
      binaryContentStorage.put(createdBinaryContent.getId(), bytes);
    } catch (RuntimeException ex) {
      log.error("Binary content upload failed: binaryContentId={}, fileName={}, contentType={}",
          createdBinaryContent.getId(), fileName, contentType, ex);
      throw ex;
    }

    log.info("Binary content uploaded: binaryContentId={}, fileName={}, size={}",
        createdBinaryContent.getId(), createdBinaryContent.getFileName(),
        createdBinaryContent.getSize());

    return binaryContentMapper.toDto(createdBinaryContent);
  }

  @Override
  public BinaryContentDto find(UUID binaryContentId) {
    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(
            () -> new BinaryContentNotFoundException(Map.of("binaryContentId", binaryContentId)));
    return binaryContentMapper.toDto(binaryContent);
  }

  @Override
  public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
    return binaryContentRepository.findAllByIdIn(binaryContentIds).stream()
        .map(binaryContentMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public void delete(UUID binaryContentId) {
    log.debug("Delete binary content requested: binaryContentId={}", binaryContentId);

    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(
            () -> new BinaryContentNotFoundException(Map.of("binaryContentId", binaryContentId)));
    binaryContentRepository.delete(binaryContent);
    log.info("Binary content deleted: binaryContentId={}", binaryContentId);
  }
}
