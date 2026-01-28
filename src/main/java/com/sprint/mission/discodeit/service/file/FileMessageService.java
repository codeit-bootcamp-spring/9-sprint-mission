package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileMessageService implements MessageService {

    private final FileMessageRepository filemessageRepository;
    private final FileChannelService fileChannelService;
    private final FileUserService fileUserService;

    public FileMessageService(
            FileMessageRepository filemessageRepository,
            FileChannelService fileChannelService,
            FileUserService fileUserService
    ) {
        this.filemessageRepository = filemessageRepository;
        this.fileChannelService = fileChannelService;
        this.fileUserService = fileUserService;
    }

    @Override
    public Message create(UUID channelId, UUID senderId, String content) {
        Message message = new Message(channelId, senderId, content);
        return filemessageRepository.save(message);
    }

    @Override
    public Message findById(UUID messageId) {
        Message message = filemessageRepository.findById(messageId);
        if (message == null) {
            throw new IllegalArgumentException("아이디" + messageId + "는 존재하지 않습니다.");
        }
        return message;
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {

        if  (channelId == null) {
            throw new IllegalArgumentException("channelId는 null일 수 없습니다.");
        }

        fileChannelService.findById(channelId);

        return filemessageRepository.findByChannelId(channelId);
    }

    @Override
    public List<Message> findAll() {
        return filemessageRepository.findAll();
    }

    @Override
    public List<Message> findBySenderId(UUID senderId) {

        if (senderId == null) {
            throw new IllegalArgumentException("senderId는 null일 수 없습니다.");
        }

        fileUserService.findById(senderId);

        return filemessageRepository.findBySenderId(senderId);
    }

    @Override
    public Message update(UUID messageId, String content) {
        Message message = findById(messageId);

        if (message == null) {
            throw new IllegalArgumentException("아이디" + messageId + "는 존재하지않습니다.");
        }

        message.updateContent(content);

        return filemessageRepository.update(message);
    }

    @Override
    public void delete(UUID messageId) {
        findById(messageId);
        filemessageRepository.delete(messageId);
    }
}
