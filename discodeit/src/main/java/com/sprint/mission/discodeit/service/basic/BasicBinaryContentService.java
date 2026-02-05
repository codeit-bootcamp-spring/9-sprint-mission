package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;

import java.util.UUID;
import java.util.List;

public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    public BasicBinaryContentService(BinaryContentRepository binaryContentRepository) {
        this.binaryContentRepository = binaryContentRepository;
    }

    @Override
    public BinaryContent create(BinaryContentCreateRequest request) {
        if (request == null || request.params() == null) {
            throw new IllegalArgumentException("request.params must not be null");
        }

        byte[] bytes = request.params().bytes();
        String contentType = request.params().contentType();
        String fileName = request.params().fileName();

        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("bytes must not be null or empty");
        }
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("contentType must not be blank");
        }
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("fileName must not be blank");
        }

        BinaryContent binaryContent = new BinaryContent(
                UUID.randomUUID(),
                bytes,
                contentType,
                fileName
        );

        return binaryContentRepository.save(binaryContent);
    }

    @Override
    public BinaryContent findById(UUID binaryContentId) {
        if (binaryContentId == null) {
            throw new IllegalArgumentException("binaryContentId must not be null");
        }

        return binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new NotFoundException("BinaryContent not found. id=" + binaryContentId));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds) {
        if (binaryContentIds == null) {
            throw new IllegalArgumentException("binaryContentIds must not be null");
        }
        if (binaryContentIds.isEmpty()) {
            return List.of();
        }

        return binaryContentRepository.findAllByIdIn(binaryContentIds);
    }
    
    @Override
    public void delete(UUID binaryContentId) {
        if (binaryContentId == null) {
            throw new IllegalArgumentException("binaryContentId must not be null");
        }

        if (!binaryContentRepository.existsById(binaryContentId)) {
            throw new NotFoundException("BinaryContent not found. id=" + binaryContentId);
        }

        binaryContentRepository.delete(binaryContentId);
    }

    @Override
    public boolean existsById(UUID binaryContentId) {
        if (binaryContentId == null) return false;
        return binaryContentRepository.existsById(binaryContentId);
    }
}
