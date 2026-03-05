package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@Profile("jcf")
@RequiredArgsConstructor
public class JCFMessageService implements MessageService {

    private final JCFUserRepository userRepository;
    private final JCFChannelRepository channelRepository;
    private final JCFMessageRepository messageRepository;
    private final MessageMapper messageMapper; // Mapper 주입

    @Override
    public MessageDto create(MessageCreateRequest request, List<MultipartFile> attachments) {

        channelRepository.findById(request.channelId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

        userRepository.findById(request.authorId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 비어 있을 수 없습니다.");
        }

        Message message = new Message(
            request.channelId(),
            request.authorId(),
            request.content()
        );

        return messageMapper.toDto(
            messageRepository.save(message)
        );
    }

    @Override
    public List<MessageDto> findAllByChannelId(UUID channelId) {

        if (channelId == null) {
            throw new IllegalArgumentException("channelId는 null일 수 없습니다.");
        }

        channelRepository.findById(channelId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

        return messageRepository.findAllByChannelId(channelId).stream()
            .map(messageMapper::toDto) // Message → MessageDto 변환
            .toList();
    }

    @Override
    public MessageDto update(UUID messageId, MessageUpdateRequest request) {

        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));

        message.updateContent(request.newContent());

        return messageMapper.toDto(
            messageRepository.update(message)
        );
    }

    @Override
    public void delete(UUID messageId) {

        messageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));

        messageRepository.delete(messageId);
    }
}