package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true) //데이터베이스 조회가 많으므로 기본적으로 읽기전용 트랜젝션 걸어줌
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  //DB에는 이름,크기만 넣고 실제파일은 폴더에 저장하기 위해 Storage불러옴
  private final BinaryContentStorage binaryContentStorage;
  //엔티티를 DTO로 예쁘게 바꿔줄 도구를 불러옴
  private final BinaryContentMapper binaryContentMapper;

  @Transactional
  @Override
  public BinaryContentDto create(BinaryContentCreateRequest request) {
    String fileName = request.fileName();
    byte[] bytes = request.bytes();
    String contentType = request.contentType();

    BinaryContent binaryContent = new BinaryContent(
        fileName,
        (long) bytes.length,
        contentType
    );

    //db에 파일 정보(이름, 사이즈 등)를 먼저 저장
    BinaryContent savedContent = binaryContentRepository.save(binaryContent);

    //방금 db에 저장되면서 생긴 id를 이름표 삼아서 실제 파일(bytes)을 로컬 폴더에 넣음
    binaryContentStorage.put(savedContent.getId(), bytes);

    return binaryContentMapper.toDto(savedContent);

  }

  @Override
  public BinaryContentDto find(UUID binaryContentId) {
    return binaryContentRepository.findById(binaryContentId)
        .map(binaryContentMapper::toDto) // /.map(entity -> binaryContentMapper.toDto(entity))
        .orElseThrow(() -> new NoSuchElementException(
            "BinaryContent with id " + binaryContentId + " not found"));
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
    if (!binaryContentRepository.existsById(binaryContentId)) {
      throw new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found");
    }
    binaryContentRepository.deleteById(binaryContentId);
  }
}
