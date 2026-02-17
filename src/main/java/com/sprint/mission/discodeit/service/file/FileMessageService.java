package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Profile("file")
@RequiredArgsConstructor
public class FileMessageService implements MessageService {

    private final FileMessageRepository fileMessageRepository;

    @Override
    public MessageResponse create(CreateMessageRequest request) {
        if (request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 비어 있을 수 없습니다.");
        }

        Message message = new Message(
                request.channelId(),
                request.senderId(),
                request.content()
        );

        return MessageResponse.from(
                fileMessageRepository.save(message)
        );
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("channelId는 null일 수 없습니다.");
        }

        return fileMessageRepository.findAllByChannelId(channelId).stream()
                .map(MessageResponse::from)
                .toList();
    }

    @Override
    public MessageResponse update(UUID messageId, UpdateMessageRequest request) {
        Message message = fileMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));

        message.updateContent(request.content());

        return MessageResponse.from(
                fileMessageRepository.update(message)
        );
    }


    @Override
    public void delete(UUID messageId) {
        Message message = fileMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));

        fileMessageRepository.delete(message.getId());
    }

}
