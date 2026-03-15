package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    @Transactional
    // 파일 생성 및 업로드
    public BinaryContentDto create(String fileName, byte[] bytes, String contentType) {

        BinaryContent content = new BinaryContent(fileName, (long) bytes.length, contentType);
        binaryContentRepository.save(content);

        binaryContentStorage.put(content.getId(), bytes); // 실제 스토리지에 파일 저장

        return binaryContentMapper.toDto(content); // DTO 변환 후 반환
    }

    @Override
    public BinaryContentDto findById(UUID id) {
        BinaryContent content = binaryContentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("BinaryContent를 찾을 수 없습니다: " + id));
        return binaryContentMapper.toDto(content); // DTO 변환
    }

    @Override
    public BinaryContent findEntityById(UUID id) {
        // 컨트롤러에서 DTO 필요 없고 엔티티만 필요한 경우
        return binaryContentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("BinaryContent를 찾을 수 없습니다: " + id));
    }

    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(ids).stream()
            .map(binaryContentMapper::toDto) // DTO 변환
            .toList();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        binaryContentRepository.deleteById(id);
    }
}