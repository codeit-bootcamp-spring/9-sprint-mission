package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@Profile("file")
@RequiredArgsConstructor
public class FileMessageService implements MessageService {

    private final FileMessageRepository fileMessageRepository;
    private final MessageMapper messageMapper; // 매퍼 주입

    @Override
    public MessageDto create(MessageCreateRequest request, List<MultipartFile> attachments) {

        if (request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 비어 있을 수 없습니다.");
        }

        Message message = new Message(
            request.channelId(),
            request.authorId(),
            request.content()
        );

        return messageMapper.toDto(fileMessageRepository.save(message));
    }

    @Override
    public List<MessageDto> findAllByChannelId(UUID channelId) {
        if (channelId == null) throw new IllegalArgumentException("channelId는 null일 수 없습니다.");

        return fileMessageRepository.findAllByChannelId(channelId).stream()
            .map(messageMapper::toDto)
            .toList();
    }

    @Override
    public MessageDto update(UUID messageId, MessageUpdateRequest request) {
        Message message = fileMessageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));

        message.updateContent(request.newContent());

        return messageMapper.toDto(fileMessageRepository.update(message));
    }

    @Override
    public void delete(UUID messageId) {
        Message message = fileMessageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));
        fileMessageRepository.delete(message.getId());
    }
}