package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentResponse create(String fileName, byte[] data, String contentType) {
        BinaryContent content = new BinaryContent(fileName, data, contentType);
        binaryContentRepository.save(content);
        return BinaryContentResponse.from(content);
    }

    @Override
    public BinaryContentResponse findById(UUID id) {
        return binaryContentRepository.findById(id)
            .map(BinaryContentResponse::from)
            .orElseThrow(() -> new IllegalArgumentException("BinaryContent를 찾을 수 없습니다: " + id));
    }

    @Override
    public BinaryContent findEntityById(UUID id) {
        return binaryContentRepository.findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException("BinaryContent를 찾을 수 없습니다: " + id)
            );
    }

    @Override
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(ids).stream()
            .map(BinaryContentResponse::from)
            .toList();
    }

    @Override
    public void delete(UUID id) {
        binaryContentRepository.delete(id);
    }
}
