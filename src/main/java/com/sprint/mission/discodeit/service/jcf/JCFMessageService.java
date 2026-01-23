package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {

    private final JCFUserService jcfUserService;
    private final JCFChannelService jcfChannelService;
    private final JCFMessageRepository jcfmessageRepository;

    public JCFMessageService(JCFUserService jcfUserService, JCFChannelService jcfChannelService, JCFMessageRepository jcfmessageRepository) {
        this.jcfUserService = jcfUserService;
        this.jcfChannelService = jcfChannelService;
        this.jcfmessageRepository = jcfmessageRepository;
    }

    @Override
    public Message create(UUID channelId, UUID senderId, String content) {

        if (jcfChannelService.findById(channelId) == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        if (jcfUserService.findById(senderId) == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 비어 있을 수 없습니다.");
        }


        Message message = new Message(channelId, senderId, content);
        return jcfmessageRepository.save(message);
    }

    @Override
    public Message findById(UUID messageId) {
        Message message = jcfmessageRepository.findById(messageId);

        if (message == null) {
            throw new IllegalArgumentException("아이디" + messageId + "는 존재하지않습니다.");
        }

        return message;
    }

    @Override
    public List<Message> findAll() {
        return jcfmessageRepository.findAll();
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {

        if  (channelId == null) {
            throw new IllegalArgumentException("channelId는 null일 수 없습니다.");
        }

        jcfChannelService.findById(channelId);

        return jcfmessageRepository.findByChannelId(channelId);
    }

    @Override
    public List<Message> findBySenderId(UUID senderId) {

        if (senderId == null) {
            throw new IllegalArgumentException("senderId는 null일 수 없습니다.");
        }

        jcfUserService.findById(senderId);

        return jcfmessageRepository.findBySenderId(senderId);
    }

    @Override
    public Message update(UUID messageId, String content) {
        Message message = findById(messageId);

        if (message == null) {
            throw new IllegalArgumentException("아이디" + messageId + "는 존재하지않습니다.");
        }

        message.updateContent(content);

        return jcfmessageRepository.update(message);
    }

    @Override
    public void delete(UUID messageId) {
        findById(messageId);
        jcfmessageRepository.delete(messageId);
    }
}
