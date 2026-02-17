package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentDTO create(UUID userId, UUID messageId, BinaryContentCreateRequest request) {
        BinaryContent content = new BinaryContent(
                userId,
                messageId,
                request.getFileName(),
                request.getContentType(),
                request.getData()
        );
        BinaryContent saved = binaryContentRepository.save(content);
        return toDto(saved);
    }

    @Override
    public BinaryContentDTO find(UUID id) {
        BinaryContent content = binaryContentRepository.find(id);
        if (content == null) {
            return null;
        }
        return toDto(content);
    }

    @Override
    public List<BinaryContentDTO> findAllByIdIn(List<UUID> ids) {
        List<BinaryContentDTO> result = new ArrayList<>();
        for (BinaryContent content : binaryContentRepository.findAllByIdIn(ids)) {
            result.add(toDto(content));
        }
        return result;
    }

    @Override
    public void delete(UUID id) {
        binaryContentRepository.delete(id);
    }

    private BinaryContentDTO toDto(BinaryContent content) {
        BinaryContentDTO dto = new BinaryContentDTO();
        dto.setId(content.getId());
        dto.setCreatedAt(content.getCreatedAt());
        dto.setData(content.getData());
        dto.setUserId(content.getUserId());
        dto.setMessageId(content.getMessageId());
        dto.setFileName(content.getFileName());
        dto.setContentType(content.getContentType());
        return dto;
    }
}
