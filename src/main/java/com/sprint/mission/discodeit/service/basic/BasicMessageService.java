package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Primary
@Profile("!jcf")
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;

    @Override
    public MessageDto create(MessageCreateRequest request,
        List<MultipartFile> attachments) {

        channelRepository.findById(request.channelId())
            .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));

        userRepository.findById(request.authorId())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<UUID> attachmentIds = new ArrayList<>();

        if (attachments != null) {
            for (MultipartFile file : attachments) {
                if (!file.isEmpty()) {
                    try {
                        String contentType = file.getContentType() != null
                            ? file.getContentType()
                            : "application/octet-stream";

                        BinaryContent binaryContent =
                            new BinaryContent(
                                file.getOriginalFilename(),
                                file.getBytes(),
                                contentType
                            );

                        binaryContentRepository.save(binaryContent);
                        attachmentIds.add(binaryContent.getId());

                    } catch (IOException e) {
                        throw new RuntimeException("파일 저장 중 오류 발생", e);
                    }
                }
            }
        }

        Message message = new Message(
            request.channelId(),
            request.authorId(),
            request.content(),
            attachmentIds
        );

        messageRepository.save(message);

        return messageMapper.toDto(message);
    }

    @Override
    public List<MessageDto> findAllByChannelId(UUID channelId) {

        channelRepository.findById(channelId)
            .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));

        return messageRepository.findAllByChannelId(channelId).stream()
            .map(messageMapper::toDto)
            .toList();
    }

    @Override
    public MessageDto update(UUID messageId, MessageUpdateRequest request) {

        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메세지입니다."));

        message.updateContent(request.newContent());
        messageRepository.update(message);

        return messageMapper.toDto(message);
    }

    @Override
    public void delete(UUID messageId) {

        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메세지입니다."));

        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.delete(attachmentId);
        }

        messageRepository.delete(messageId);
    }
}