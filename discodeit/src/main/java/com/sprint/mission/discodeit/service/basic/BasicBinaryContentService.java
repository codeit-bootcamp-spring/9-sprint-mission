package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentMapper binaryContentMapper;

  @Override
  @Transactional
  public BinaryContentDto create(BinaryContentCreateRequest request) {
    BinaryContent binaryContent = new BinaryContent(
        request.fileName(),
        request.size(),
        request.contentType()
    );
    BinaryContent saved = binaryContentRepository.save(binaryContent);

    // 스토리지 인터페이스의 UUID 반환 규격 준수
    binaryContentStorage.put(saved.getId(), request.bytes());

    return binaryContentMapper.toDto(saved);
  }

  @Override
  public BinaryContentDto findById(UUID id) {
    return binaryContentRepository.findById(id)
        .map(binaryContentMapper::toDto)
        .orElseThrow(() -> new NoSuchElementException("파일 정보를 찾을 수 없습니다. ID: " + id));
  }

  @Override
  public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
    // [수정] findAllByIdIn -> findAllById 로 변경
    // JpaRepository에서 기본으로 제공하는 표준 메서드를 사용합니다.
    return binaryContentRepository.findAllById(ids).stream()
        .map(binaryContentMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    binaryContentRepository.deleteById(id);
  }

  @Override
  public ResponseEntity<Resource> download(UUID binaryContentId) {
    BinaryContentDto contentDto = findById(binaryContentId);
    return binaryContentStorage.download(contentDto);
  }
}