package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.DTO.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.status.BinaryContentInterface;
import com.sprint.mission.discodeit.entity.BinaryContent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentInterface binaryContentInterface;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Override
    public void delete(UUID id) {
        binaryContentInterface.deleteById(id);
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return binaryContentInterface.findAllByIdIn(ids);
    }

    @Override
    public BinaryContent find(UUID id) {
        return binaryContentInterface.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent 없음"));
    }

    @Override
    public BinaryContent create(BinaryContentCreateRequest request) {
        if (!userRepository.existsById(request.userId())) {
            throw new NoSuchElementException("없는 유저");
        }
        if (!messageRepository.existsById(request.messageId())) {
            throw new NoSuchElementException("없는 메세지");
        }

        BinaryContent binaryContent = new BinaryContent(
                UUID.randomUUID(),
                request.userId(),
                request.messageId(),
                Instant.now()
        );
        binaryContentInterface.save(binaryContent);
        return binaryContent;
    }
}
