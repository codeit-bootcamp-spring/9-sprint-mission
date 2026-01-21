package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public BasicMessageService(MessageRepository messageRepository,
                               ChannelRepository channelRepository,
                               UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Message create(String content, UUID channelId, UUID authorId) {

//        // ✅ 심화 요구사항 힌트(관련 데이터 확인) 최소 적용:
//        // 채널/유저가 없으면 메시지 생성 자체를 막는다.
//        if (channelRepository.findById(channelId) == null) {
//            throw new IllegalArgumentException("존재하지 않는 채널입니다: " + channelId);
//        }
//        if (userRepository.findById(authorId) == null) {
//            throw new IllegalArgumentException("존재하지 않는 유저입니다: " + authorId);
//        }

        Message message = new Message(content, channelId, authorId);
        messageRepository.create(message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return messageRepository.findById(id);
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return messageRepository.findByChannelId(channelId);
    }

    @Override
    public boolean update(UUID id, String content) {
        return messageRepository.update(id, content);
    }

    @Override
    public boolean delete(UUID id) {
        return messageRepository.delete(id);
    }
}

