package sprintMission1.service.jcf;

import sprintMission1.entity.Channel;
import sprintMission1.entity.Message;
import sprintMission1.entity.User;
import sprintMission1.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {

    private final Map<UUID,Message> messages;

    private void printMessage(Message message) {
        System.out.println("--------" + message + "--------");
        System.out.println("UID: " + message.getId());
        System.out.println("이름: " + message.getOwner().getUserName());
        System.out.println("내용: " + message.getContent());
        System.out.println("생성일자: " + message.getCreatedAt());
        System.out.println("수정일자: " + message.getUpdatedAt());
        System.out.println("--------" + message + "--------");
    }

    public JCFMessageService() {
        this.messages = new HashMap<>();
    }

    public Message create(String content, User owner, Channel channel) {
        Message message = new Message(content, owner);
        messages.put(message.getId(), message);
        channel.addMessage(message);
        return message;
    }

    public void read(UUID messageId) {
        Message message = messages.get(messageId);
        if (message != null) {
            printMessage(message);
        } else {
            System.out.println("메세지가 존재하지 않습니다.");
        }
    }

    public void readAll() {
        for (Message message : messages.values()) {
            printMessage(message);
        }
    }

    public void update(UUID messageId, String content) {
        Message message = messages.get(messageId);
        if (message != null) {
            message.update(content);
        } else {
            System.out.println("메세지가 존재하지 않습니다.");
        }
    }

    public void delete(UUID messageId) {
        messages.remove(messageId);
    }

}
