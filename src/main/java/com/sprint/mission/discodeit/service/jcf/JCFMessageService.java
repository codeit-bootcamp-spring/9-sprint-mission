package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final List<Message> messages = new ArrayList<>();

    //등록? 생성?
    @Override
    public Message create(UUID userId, UUID channelId, String content) {
        Message message = new Message(content, userId, channelId);
        messages.add(message);
        return message;
    }

    //단건조회
    @Override
    public Message find(UUID messageId) {
        for (Message message : messages) {
            if (message.getId().equals(messageId)) {
                return message;
            }
        }
        return null;
    }

    //다건조회
    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messages);
    }

    //수정
    @Override
    public Message update(UUID messageId, String content) {
        Message message = find(messageId); // 메시지 찾기
        if (message != null) {
            message.update(content);       // content만 수정
            return message;
        }
        return null; // 메시지를 찾지 못하면 null 반환
    }

    //삭제
    @Override
    public void delete (UUID messageId){
        Message message = find(messageId);
        if (message != null) {
            messages.remove(message);
        }
    }
}
