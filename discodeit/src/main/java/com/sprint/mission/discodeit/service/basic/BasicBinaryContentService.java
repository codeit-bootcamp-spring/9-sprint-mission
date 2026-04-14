package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;

  @Override
  @Transactional
  public BinaryContentDto create(BinaryContentCreateRequest request) {
    log.debug("binaryContent 생성 시작: {}", request);
    String fileName = request.fileName();
    byte[] bytes = request.bytes();
    String contentType = request.contentType();
    BinaryContent binaryContent = new BinaryContent(
        fileName,
        (long) bytes.length,
        contentType
    );
    BinaryContentDto dto = binaryContentMapper.toDto(binaryContentRepository.save(binaryContent));
    log.info("binaryContent 생성 완료: {}", dto);
    return dto;
  }

  @Override
  public BinaryContentDto find(UUID binaryContentId) {
    return binaryContentMapper.toDto(
        binaryContentRepository.findById(binaryContentId).orElseThrow(() -> {
          log.warn("존재하지 않는 binaryContentId: {}", binaryContentId);
          return new BinaryContentNotFoundException(ErrorCode.BINARY_CONTENT_NOT_FOUND,
              Map.of("binaryContentId", binaryContentId));
        }));
  }

  @Override
  public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
    return binaryContentRepository.findAllByIdIn(binaryContentIds).stream()
        .map(binaryContentMapper::toDto).toList();
  }

  @Override
  @Transactional
  public void delete(UUID binaryContentId) {
    log.warn("binaryContent 삭제 시도: {}", binaryContentId);
    if (!binaryContentRepository.existsById(binaryContentId)) {
      log.warn("해당 binaryContentId가 존재하지 않음: {}", binaryContentId);
      throw new BinaryContentNotFoundException(ErrorCode.BINARY_CONTENT_NOT_FOUND,
          Map.of("binaryContentId", binaryContentId));
    }
    binaryContentRepository.deleteById(binaryContentId);
    log.info("binaryContent 삭제 완료: {}", binaryContentId);
  }
}
