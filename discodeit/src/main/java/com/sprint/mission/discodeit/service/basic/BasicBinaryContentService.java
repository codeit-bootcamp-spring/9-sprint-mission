package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.BinaryContentException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final BinaryContentStorage binaryContentStorage;


  @Override
  public BinaryContentDto create(BinaryContentCreateRequest request) {
    log.info("파일 업로드(생성) 로직 시작 - 파일명: {}, 크기: {} bytes", request.fileName(),
        request.bytes().length);
    // DB에 저장할 엔티티 생성
    BinaryContent binaryContent = new BinaryContent(
        request.fileName(),
        (long) request.bytes().length,
        request.contentType()
    );

    // 먼저 DB(장부)에 파일 정보를 저장합니다.
    BinaryContent savedEntity = binaryContentRepository.save(binaryContent);

    // DB에서 생성된 ID(savedEntity.getId())를 열쇠로 해서 저장합니다.
    binaryContentStorage.put(savedEntity.getId(), request.bytes());

    log.info("파일 업로드 완료 및 DB/스토리지 저장 성공 - 파일 ID: {}", savedEntity.getId());
    // 매퍼를 통해 DTO로 변환해서 반환합니다.
    return binaryContentMapper.toDto(savedEntity);
  }

  @Override
  public BinaryContentDto find(UUID binaryContentId) {
    log.info("파일 단건 조회 로직 시작 - 대상 파일 ID: {}", binaryContentId);
    return binaryContentRepository.findById(binaryContentId)
        .map(binaryContentMapper::toDto) // Entity -> Dto 변환
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
        .map(binaryContentMapper::toDto) // 리스트의 각 항목을 Dto로 변환
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
