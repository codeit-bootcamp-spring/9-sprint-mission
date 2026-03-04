package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(MessageCreateRequest request, List<BinaryContentCreateRequest> attachmentRequests) {
        List<UUID> attachmentIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(attachmentRequests)) {
            for (BinaryContentCreateRequest attachmentRequest : attachmentRequests) {
                try {
                    byte[] bytes = attachmentRequest.inputStream().readAllBytes();
                    BinaryContent attachment = new BinaryContent(
                            attachmentRequest.fileName(),
                            attachmentRequest.contentType(),
                            bytes
                    );
                    attachmentIds.add(binaryContentRepository.save(attachment).getId());
                } catch (IOException e) {
                    throw new RuntimeException("첨부파일 처리 중 오류가 발생했습니다.", e);
                }
            }
        }

        Message message = new Message(
                request.content(),
                request.channelId(),
                request.authorId(),
                attachmentIds
        );
        return messageRepository.save(message);
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId);
    }

    @Override
    public Message update(UUID id, MessageUpdateRequest request) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + id + " not found"));
        message.update(request.newContent());
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + id + " not found"));

        if (!CollectionUtils.isEmpty(message.getAttachmentIds())) {
            for (UUID attachmentId : message.getAttachmentIds()) {
                binaryContentRepository.deleteById(attachmentId);
            }
        }

        messageRepository.deleteById(id);
    }
}
