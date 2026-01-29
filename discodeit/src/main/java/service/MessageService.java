package service;

import entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    //생성
    Message create(UUID channaId, UUID senderId, String content);

    //수정
    Message update(UUID messageId, String content);

    //단건 조회
    Message findById(UUID messageId);

    //전체 조회
    List<Message> findAll();

    //삭제
    void delete(UUID messageId);

    //확인여부
    boolean existsById(UUID messageId);
}
