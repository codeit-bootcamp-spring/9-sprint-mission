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
    log.info("파일 업로드 시작 - fileName: {}, contentType: {}, size: {} bytes", 
        fileName, contentType, bytes.length);
    BinaryContent binaryContent = new BinaryContent(
        fileName,
        (long) bytes.length,
        contentType
    );
    binaryContentRepository.save(binaryContent);
    binaryContentStorage.put(binaryContent.getId(), bytes);
    log.info("파일 업로드 완료 - binaryContentId: {}", binaryContent.getId());

    return binaryContentMapper.toDto(binaryContent);
  }

  @Override
  public BinaryContentDto find(UUID binaryContentId) {
    log.debug("파일 다운로드 시작 - binaryContentId: {}", binaryContentId);
    return binaryContentRepository.findById(binaryContentId)
        .map(binaryContent -> {
          log.info("파일 다운로드 완료 - binaryContentId: {}, fileName: {}", 
              binaryContentId, binaryContent.getFileName());
          return binaryContentMapper.toDto(binaryContent);
        })
        .orElseThrow(() -> {
          log.error("파일 다운로드 실패 - 파일을 찾을 수 없음: {}", binaryContentId);
          return new BinaryContentNotFoundException(binaryContentId);
        });
  }

  @Override
  public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
    return binaryContentRepository.findAllById(binaryContentIds).stream()
        .map(binaryContentMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public void delete(UUID binaryContentId) {
    log.info("파일 삭제 시작 - binaryContentId: {}", binaryContentId);
    if (!binaryContentRepository.existsById(binaryContentId)) {
      log.error("파일 삭제 실패 - 파일을 찾을 수 없음: {}", binaryContentId);
      throw new BinaryContentNotFoundException(binaryContentId);
    }
    binaryContentRepository.deleteById(binaryContentId);
    log.info("파일 삭제 완료 - binaryContentId: {}", binaryContentId);
  }
}
