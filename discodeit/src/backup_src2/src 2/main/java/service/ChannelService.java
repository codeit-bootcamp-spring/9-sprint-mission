package service;

import entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {


        //생성
        Channel create(String frame, String channelName, String detail);

        // 검색
        Channel find(UUID id);

        // 전체 출력
        List<Channel> findAll();

        // 수정
        Channel update(UUID id,String frame, String channelName, String detail);

        // 삭제
        void delete (UUID id);
}
