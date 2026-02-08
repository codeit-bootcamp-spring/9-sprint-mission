package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.DTO.MessageDTO;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private List<Message> data = new ArrayList<>();


//    @Override
//    public Message createMessage(String text, UUID channelId, UUID authorId) {
//        Message message = new Message(authorId, channelId, text);
//        data.add(message);
//        return message;
//    }


    @Override
    public Message createMessage(MessageDTO.CreateMessageDTO dto) {
        Message message = new Message(dto.authorId(),dto.channelId(),dto.text(),dto.attachmentIds());
        data.add(message);
        return message;
    }

    @Override
    public Message findByMessageId(UUID messageId) {
        for (Message message : data) {
            if (message.getMessageId().equals(messageId)) {
                return message;
            }
        }
        throw new RuntimeException("Message not found");
    }

    @Override
    public Message findByChannelId(UUID channelId) {
        if (data.isEmpty()) {
            System.out.println("해당하는 채널이 없습니다.");
        }
        return (Message) data;
    }

    @Override
    public List<Message> findAllMessage() {
        if (data.isEmpty()) {
            System.out.println("메시지가 없습니다.");
        }
        return data;
    }

    @Override
    public Message updateMessage(UUID messageId, String text) {

            for (Message message : data) {
                if (message.getMessageId().equals(messageId)){
                    message.updateMessage(text);
                    return message;
                }
            }
            if (findByMessageId(messageId) == null) {
                throw new RuntimeException("해당 ID의 메시지가 없습니다.");
            }
            throw new RuntimeException("수정할 수 없습니다.");
        }

    @Override
    public boolean deleteMessage(UUID messageId) {
        Message message = findByMessageId(messageId);
        if (message == null) {
            System.out.println("삭제할 메시지가 없습니다.");
            return false;
        }
        return data.remove(message);
    }

}
