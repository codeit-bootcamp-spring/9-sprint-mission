package service;

import entity.Message;
import java.util.List;
import java.util.UUID;

// 인터페이스 : 어떤 역할을 수행할 것인지 정해둔 메뉴판, 목록만 정의하는 곳, 실제 행동은 하지 않음
public interface MessageService {

    Message create(String content, UUID channelId, UUID authorId);

    Message find(UUID id);

    List<Message> findAll();

    Message update(UUID id, String content);

    void delete(UUID id);
}