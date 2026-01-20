package service;

import entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    //생성
    Channel create(String name, UUID ownerId);

    //수정
    Channel update(UUID channelId, String name);

    //단건조회
    Channel findById(UUID channelId);

    //전체조회
    List<Channel> findAll();

    //삭제
    void delete(UUID channelId);

    //확인용
    boolean existsById(UUID channelId);
}
