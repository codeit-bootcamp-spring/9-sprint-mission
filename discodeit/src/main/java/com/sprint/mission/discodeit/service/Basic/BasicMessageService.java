package com.sprint.mission.discodeit.service.Basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    @Override
    public Message create(UUID channelId, UUID authorId, String content) {
        Message newMessage = new Message(channelId, authorId, content);
        messageRepository.save(newMessage);
        UUID newMsgId = newMessage.getId();

        Channel channel = channelRepository.findByID(channelId);
        if (!channel.addMessage(newMsgId)){
            messageRepository.remove(newMsgId);
        }
        channelRepository.save(channel);
        return newMessage;
    }

    @Override
    public void remove(UUID id) {
        Message removeMessage = messageRepository.findByID(id);
        messageRepository.remove(id);

        UUID channelId = removeMessage.getChannelId();
        Channel channel = channelRepository.findByID(channelId);
        if (!channel.removeMessage(id)){
            messageRepository.save(removeMessage);
        }
    }

    @Override
    public Message findByID(UUID id) {
        return messageRepository.findByID(id);
    }

    @Override
    public List<Message> getAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message updateContent(UUID id, String newContent) {
        Message target = messageRepository.findByID(id);
        if (target == null){
            throw new IllegalStateException("메시지 수정 실패 (해당 메시지가 존재하지 않음) | 메시지ID: " + id);
        }
        target.updateContent(newContent);
        messageRepository.save(target);
        return target;
    }
}
