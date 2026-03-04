package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(MessageCreateRequest request, List<UUID> attachmentIds) {

        Channel channel = channelRepository.findByID(request.channelId()).orElseThrow();

        Message newMessage = new Message(request.channelId(),
                request.authorId(),
                request.content(),
                attachmentIds
        );
        messageRepository.save(newMessage);
        UUID newMsgId = newMessage.getId();

        if (!channel.addMessage(newMsgId)){
            messageRepository.remove(newMsgId);
            throw new IllegalArgumentException("메시지 삭제 실패 (Channel::addMessage 오류) | 메시지ID: \" + id");
        }
        channelRepository.save(channel);
        return newMessage;
    }

    @Override
    public void remove(UUID id) {
        Message removeMessage = messageRepository.findByID(id).orElseThrow();

        List<UUID> attachmentIdList = removeMessage.getAttachmentIds();
        List<BinaryContent> attachmentList = new ArrayList<>();
        if (attachmentIdList != null && !attachmentIdList.isEmpty()) {
            for (UUID fileId : attachmentIdList) {
                BinaryContent content = binaryContentRepository.findById(fileId).orElseThrow();
                attachmentList.add(content);
            }
            attachmentIdList.forEach(binaryContentRepository::deleteById);
        }
        messageRepository.remove(id);

        UUID channelId = removeMessage.getChannelId();
        Channel channel = channelRepository.findByID(channelId).orElseThrow();
        if (!channel.removeMessage(id)){
            messageRepository.save(removeMessage);
            attachmentList.forEach(binaryContentRepository::save);
            throw new IllegalStateException("메시지 삭제 실패 (Channel::removeMessage 오류) | 메시지ID: " + id);
        }
    }

    @Override
    public Message findByID(UUID id) {
        return messageRepository.findByID(id).orElseThrow();
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public Message updateContent(UUID id, String newContent) {
        Message target = messageRepository.findByID(id).orElseThrow();
        target.updateContent(newContent);
        messageRepository.save(target);
        return target;
    }
}
