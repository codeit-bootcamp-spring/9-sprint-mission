package service;

import entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // 생성
    boolean addChannel (Channel channel);

    // 조회
    Channel getChannelById (UUID id);
    Channel getChannelByName (String name);
    List<Channel> getallChannels();
    List<Channel> getChannelsByOwnerId(String ownerId);
    List<Channel> searchChannels(String nameorownerId);

    // 수정
    Channel updateChannel (String oldName, String newName, String description);

    // 삭제
    boolean deleteChannel (String name);


    Channel getChannelByownerId(String channelId);

    Channel getChannelById(String channelId);
}
