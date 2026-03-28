package com.sprint.mission.discodeit.repository.jcf;


import com.sprint.mission.discodeit.dto.response.ResponseMessageDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exepction.FailedDelete;
import com.sprint.mission.discodeit.exepction.global.NotFound;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, List<Message>> channelIdMessageMap = new ConcurrentHashMap<>();
    private final Map<UUID, List<Message>> userIdMessageMap = new ConcurrentHashMap<>();
    private final Map<UUID, Message> messageIdMap = new ConcurrentHashMap<>(128);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초").withZone(ZoneId.of("Asia/Seoul"));

    @Override
    public ResponseMessageDto create(String content, UUID channelId, UUID userId, List<UUID> attachmentIdList) {
        Message message = new Message(channelId, userId, content, attachmentIdList);

        messageIdMap.put(message.getId(), message);
        channelIdMessageMap.computeIfAbsent(channelId, m -> new ArrayList<>()).add(message);
        userIdMessageMap.computeIfAbsent(userId, m -> new ArrayList<>()).add(message);

        return new ResponseMessageDto(
                message.getId(),
                message.getChannelId(),
                message.getUserId(),
                message.getBinaryContentIds(),
                FORMATTER.format(message.getCreateAt()),
                FORMATTER.format(message.getUpdateAt()),
                message.getContent());
    }

    @Override
    public List<ResponseMessageDto> findAllInChannel(UUID channelId) {
        List<ResponseMessageDto> result = new ArrayList<>();
        try{
            List<Message> messages = channelIdMessageMap.get(channelId);
            messages.stream().sorted(Comparator.comparing(Message::getCreateAt))
                    .forEach(message -> {
                        result.add(new ResponseMessageDto(
                                message.getId(),
                                message.getChannelId(),
                                message.getUserId(),
                                message.getBinaryContentIds(),
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
            throw new NotFound("Last message not found");
        }
    }

    @Override
    public List<ResponseMessageDto> findAllForSender(UUID userId) {
        List<ResponseMessageDto> result = new ArrayList<>();
        List<Message> messages = userIdMessageMap.get(userId);
        try{
            if(messages != null) {
                messages.stream().sorted(Comparator.comparing(Message::getCreateAt))
                        .forEach(message -> {
                            result.add(new ResponseMessageDto(
                                    message.getId(),
                                    message.getChannelId(),
                                    message.getUserId(),
                                    message.getBinaryContentIds(),
                                    FORMATTER.format(message.getCreateAt()),
                                    FORMATTER.format(message.getUpdateAt()),
                                    message.getContent()
                            ));
                        });
            }
        } catch (Exception e) {
            throw new NotFound("Message not found");
        }
        return result;
    }

    @Override
    public ResponseMessageDto updateMessage(UUID id, String content) {
        Message message = messageIdMap.get(id);
        if (message == null) throw new NotFound("Message not found");

        message.updateMessage(content);

        return new ResponseMessageDto(
                message.getId(),
                message.getChannelId(),
                message.getUserId(),
                message.getBinaryContentIds(),
                FORMATTER.format(message.getCreateAt()),
                FORMATTER.format(message.getUpdateAt()),
                message.getContent());
    }

    @Override
    public UUID delete(UUID userId, UUID id) {
        List<Message> userMessages = userIdMessageMap.get(userId);
        if (userMessages == null) throw new NotFound("Message not found(Delete)");

        Message message = userMessages.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
        if (message == null) throw new NotFound("Message not found(Delete)");

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
                result.add(message.getBinaryContentIds());
                delete(message.getUserId(), message.getId());
            });
        }
        if(userIdMessageMap.containsKey(id)) {
            new ArrayList<>(userIdMessageMap.get(id)).forEach(message -> {
                result.add(message.getBinaryContentIds());
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
