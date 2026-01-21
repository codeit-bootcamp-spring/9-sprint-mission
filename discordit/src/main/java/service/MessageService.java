package service;

import entity.Message;

import java.util.List;

public interface MessageService {
    // 생성
    boolean addMessage(Message message);  // void → boolean

    // 조회
    Message getChannelId(String ChannelId);
    Message getContent(String Content);
    List<Message> getUsername(String userName);
    List<Message> getAllMessages();

    // 수정
    Message updateMassage(String oldcontent,String newcontent ,String userName, String channelId);

    // 삭제
    boolean deleteMessage(String message);

    // 의존성 검사
//    Message createMessage(String userId, String channelId, String content);
}
