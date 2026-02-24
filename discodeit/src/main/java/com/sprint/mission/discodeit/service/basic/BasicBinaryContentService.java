package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContent create(BinaryContentCreateRequest request) {
        byte[] originalBytes = request.bytes();
        BinaryContent content = new BinaryContent(
                request.fileName(),
                request.contentType(),
                (long) originalBytes.length,
                originalBytes
        );
        return binaryContentRepository.save(content);
    }

    @Override
    public BinaryContent find(UUID id) {
        return getBinaryContent(id);
    }

    @Override
    public List<BinaryContent> findAllByIds(List<UUID> ids) {
        return ids.stream()
                .map(this::find) // 각 ID를 find 메서드에 넣어 BinaryContent로 변환
                .toList();
    }

    public BinaryContent getBinaryContent(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("파일을 찾을 수 없습니다."));
    }
}