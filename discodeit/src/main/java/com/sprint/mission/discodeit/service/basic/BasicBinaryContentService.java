package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.BinaryContent;
import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentResponse create(BinaryContentCreateRequest request) {
        BinaryContent content = new BinaryContent(
                UUID.randomUUID(),
                request.data(),
                request.fileName(),
                request.userId(),    // 없을 경우 null
                request.messageId(), // 없을 경우 null
                Instant.now()
        );
        binaryContentRepository.save(content);
        return toResponse(content);
    }

    @Override
    public BinaryContentResponse find(UUID binaryContentId) {
        BinaryContent content = binaryContentRepository.findById(binaryContentId);
        if (content == null) {
            throw new NoSuchElementException("파일을 찾을 수 없습니다: " + binaryContentId);
        }
        return toResponse(content);
    }

    @Override
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> ids) {
        // Repository에 추가한 findAllByIdIn 메서드 사용
        return binaryContentRepository.findAllByIdIn(ids).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID binaryContentId) {
        if (binaryContentRepository.findById(binaryContentId) == null) {
            throw new NoSuchElementException("파일을 찾을 수 없습니다.");
        }
        binaryContentRepository.deleteById(binaryContentId);
    }

    // 변환기
    private BinaryContentResponse toResponse(BinaryContent content) {
        return new BinaryContentResponse(
                content.getId(),
                content.getFileName(),
                content.getData() != null ? content.getData().length : 0, // 사이즈 계산
                content.getUserId(),
                content.getMessageId()
        );
    }
}