package com.sprint.mission.discodeit.repository.jcf;


import com.sprint.mission.discodeit.dto.MessageRequestDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exepction.FailedDelete;
import com.sprint.mission.discodeit.exepction.FailedFound;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("jcf")
public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, List<Message>> channelIdMessageMap = new ConcurrentHashMap<>();
    private final Map<UUID, List<Message>> userIdMessageMap = new ConcurrentHashMap<>();
    private final Map<UUID, Message> messageIdMap = new ConcurrentHashMap<>(128);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초").withZone(ZoneId.of("Asia/Seoul"));

    @Override
    public UUID create(String content, UUID channelId, UUID userId, List<UUID> attachmentIdList) {
        Message message = new Message(channelId, userId, content, attachmentIdList);

        messageIdMap.put(message.getId(), message);
        channelIdMessageMap.computeIfAbsent(channelId, m -> new ArrayList<>()).add(message);
        userIdMessageMap.computeIfAbsent(userId, m -> new ArrayList<>()).add(message);

        return message.getId();
    }

    @Override
    public List<MessageRequestDto> findAllInChannel(UUID channelId) {
        List<MessageRequestDto> result = new ArrayList<>();
        try{
            List<Message> messages = channelIdMessageMap.get(channelId);
            messages.stream().sorted(Comparator.comparing(Message::getCreateAt))
                    .forEach(message -> {
                        result.add(new MessageRequestDto(
                                message.getId(),
                                message.getChannelId(),
                                message.getUserId(),
                                message.getAttachmentIds(),
                                FORMATTER.format(message.getCreateAt()),
                                FORMATTER.format(message.getUpdateAt()), message.getContent()
                        ));
                    });
            return result;
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public Instant getLastMessageInChannel(UUID channelId) {
        try {
            return channelIdMessageMap.get(channelId)
                    .stream().max(Comparator.comparing(Message::getCreateAt)).orElse(null).getCreateAt();
        } catch (NullPointerException e) {
            throw new FailedFound("Last message not found");
        }
    }

    @Override
    public List<MessageRequestDto> findAllForSender(UUID userId) {
        List<MessageRequestDto> result = new ArrayList<>();
        List<Message> messages = userIdMessageMap.get(userId);
        try{
            if(messages != null) {
                messages.stream().sorted(Comparator.comparing(Message::getCreateAt))
                        .forEach(message -> {
                            result.add(new MessageRequestDto(
                                    message.getId(),
                                    message.getChannelId(),
                                    message.getUserId(),
                                    message.getAttachmentIds(),
                                    FORMATTER.format(message.getCreateAt()),
                                    FORMATTER.format(message.getUpdateAt()),
                                    message.getContent()
                            ));
                        });
            }
        } catch (Exception e) {
            throw new FailedFound("Message not found");
        }
        return result;
    }

    @Override
    public boolean updateMessage(UUID id, String content) {
        Message message = messageIdMap.get(id);
        if (message == null) throw new FailedFound("Message not found");

        message.updateMessage(content);

        return true;
    }

    @Override
    public UUID delete(UUID userId, UUID id) {
        List<Message> userMessages = userIdMessageMap.get(userId);
        if (userMessages == null) throw new FailedFound("Message not found(Delete)");

        Message message = userMessages.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
        if (message == null) throw new FailedFound("Message not found(Delete)");

        UUID channelId = message.getSendChannelId();
        try {
            userIdMessageMap.get(userId).remove(message);

            if(channelIdMessageMap.containsKey(channelId)) {
                channelIdMessageMap.get(channelId).remove(message);
            }
            messageIdMap.remove(message.getId());
            return message.getId();
        } catch (Exception e) {
            throw new FailedDelete("Message delete failed");
        }
    }

    @Override
    public List<List<UUID>> deleteAll(UUID id) {
        List<List<UUID>> result = new ArrayList<>();
        if(channelIdMessageMap.containsKey(id)) {
            new ArrayList<>(channelIdMessageMap.get(id)).forEach(message -> {
                result.add(message.getAttachmentIds());
                delete(message.getUserId(), message.getId());
            });
        }
        if(userIdMessageMap.containsKey(id)) {
            new ArrayList<>(userIdMessageMap.get(id)).forEach(message -> {
                result.add(message.getAttachmentIds());
                delete(message.getUserId(), message.getId());
            });
        }
        return result;
    }

    @Override
    public boolean isPresentMessage(UUID userId, UUID id) {
        List<Message> messages = userIdMessageMap.get(userId);
        if(messages == null) return false;

        Object result = messages.stream().filter(message -> message.getId().equals(id)).findFirst().orElse(null);
        return result != null;
    }
}
