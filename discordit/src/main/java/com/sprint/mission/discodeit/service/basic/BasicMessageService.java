package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateMessageDto;
import com.sprint.mission.discodeit.dto.MessageRequestDto;
import com.sprint.mission.discodeit.exepction.FailedFound;
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
    public boolean create(CreateMessageDto requestDto) {
        String text = requestDto.text();
        String sendeeChannelName = requestDto.sendeeChannelName();
        String senderUserName = requestDto.senderUserName();
        List<UUID> binaryContentIds = requestDto.binaryContentIds();

        UUID channelId = channelRepository.getChannelId(sendeeChannelName);
        UUID userId = userRepository.userNameToId(senderUserName);
        messageRepository.create(text, channelId, userId, binaryContentIds);

        return true;
    }

    @Override
    public List<String> findAllForSender(UUID userId) {
        return messageRepository.findAllForSender(userId).stream().map(this::formattingMessage).toList();
    }

    @Override
    public List<String> findAllInChannel(String channelName) {
        UUID channelId = channelRepository.channelNameToId(channelName);
        return messageRepository.findAllInChannel(channelId).stream().map(this::formattingMessage).toList();
    }

    private String formattingMessage(MessageRequestDto dto) {
        String id = "ID: " + dto.id().toString();
        String user = "사용자명: " + userRepository.userIdToName(dto.userId());
        String channel = "채널명: " + channelRepository.channelIdToName(dto.channelId());

        return id + "\n"
                + user + "\n"
                + channel + "\n"
                + "내용: " + dto.content() + "\n"
                + "첨부파일: " + String.join("", binaryContentRepository.findAllByIdIn(dto.attachmentIds())) + "\n"
                + "생성일: " + dto.createAt() + "\n"
                + "수정일: " + dto.updateAt() + "\n====================";
    }

    @Override
    public boolean update(UUID userId, UUID messageId, String content) {
        if (messageRepository.isPresentMessage(userId, messageId)) {
            throw new FailedFound("해당 ID를 찾지 못했습니다.");
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
    public String lastMessageTime(String channelName) {
        UUID channelId = channelRepository.channelNameToId(channelName);
        return messageRepository.findAllInChannel(channelId).stream().map(MessageRequestDto::createAt).max(String::compareTo).orElse("없음");
    }
}
