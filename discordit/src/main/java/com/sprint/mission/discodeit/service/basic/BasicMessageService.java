package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.RequestCreateMessageDto;
import com.sprint.mission.discodeit.dto.response.ResponseMessageDto;
import com.sprint.mission.discodeit.exepction.global.NotFound;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UUID nullUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public boolean isPresent(UUID userId, UUID messageId) {
        return messageRepository.isPresentMessage(userId, messageId);
    }

    @Override
    public ResponseMessageDto create(RequestCreateMessageDto requestDto) {
        String text = requestDto.text();
        UUID channelId = requestDto.channelId();
        UUID userId = requestDto.userId();
        List<UUID> binaryContentIds = requestDto.binaryContentIds();

        return messageRepository.create(text, channelId, userId, binaryContentIds);
    }

    @Override
    public List<String> findAllForSender(UUID userId) {
        return messageRepository.findAllForSender(userId).stream().map(this::formattingMessage).toList();
    }

    @Override
    public List<ResponseMessageDto> findAllInChannel(UUID channelId) {
        return messageRepository.findAllInChannel(channelId);
    }

    private String formattingMessage(ResponseMessageDto dto) {
        String id = "ID: " + dto.messageId().toString();
        String user = "사용자명: " + userRepository.userIdToName(dto.userId());
        String channel = "채널명: " + channelRepository.channelIdToName(dto.channelId());

        return id + "\n"
                + user + "\n"
                + channel + "\n"
                + "내용: " + dto.content() + "\n"
                + "첨부파일: " + String.join("", binaryContentRepository.findAllByIdIn(dto.binaryContentIds()).toString()) + "\n"
                + "생성일: " + dto.createAt() + "\n"
                + "수정일: " + dto.updateAt() + "\n====================";
    }

    @Override
    public ResponseMessageDto update(UUID userId, UUID messageId, String content) {
        if (!messageRepository.isPresentMessage(userId, messageId)) {
            throw new NotFound("해당 ID를 찾지 못했습니다.");
        }

        return messageRepository.updateMessage(messageId, content);
    }

    @Override
    public boolean delete(UUID userId, UUID messageId) {
        UUID delete = messageRepository.delete(userId, messageId);
        if(delete.equals(nullUUID)) return false;
        binaryContentRepository.delete(delete);
        return true;
    }

    @Override
    public void deleteAll(UUID id) {
        messageRepository.deleteAll(id).forEach(binaryContentRepository::delete);
    }

    @Override
    public String lastMessageTime(UUID channelId) {
        return messageRepository.findAllInChannel(channelId).stream().map(ResponseMessageDto::createAt).max(String::compareTo).orElse("없음");
    }
}
