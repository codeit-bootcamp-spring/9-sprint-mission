package service;

import entity.Channel;

import java.util.List;
import java.util.UUID;


public interface ChannelService {

    //생성
    Channel addChannel(Channel.ChannelType channelType,String channelName, String description);

    //조회
    Channel getChannel(UUID id);

    //전체조회
    List<Channel> getAllChannel();

    //수정
    Channel updateChannel(UUID id, String channelName, String description);

    //삭제
    void deleteChannel(UUID id);
}
