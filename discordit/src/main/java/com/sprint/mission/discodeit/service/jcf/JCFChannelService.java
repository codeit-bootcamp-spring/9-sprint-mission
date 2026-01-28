package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> channels = new ArrayList<>();

    @Override
    public Channel create(String displayName,ChannelType type) {

        try {
            Channel channel = new Channel(displayName,type);
            channels.add(channel);
            System.out.println("채널 등록 성공 : " + displayName);

            return channel;

        } catch (Exception e) {
            System.out.println("채널 등록 실패");
            return null;

        }
    }

    @Override
    public Channel find(UUID id) {
        for (Channel channel : channels) {
            if (channel.getId() == id){
                return channel;
            }
        }
                 return null;
    }

    @Override
    public List<Channel> findAll() {return new ArrayList<>(channels);}
    /* ArrayList쓴 이유 외부에서 함부로 건드리지 못하게 보호하고  외부 리스트 항목을 추가하거나 삭제해도
    관리하는 원본 list에 영향을 주지 않을려고 했다
    findall메서드의
    */


    @Override
    public Channel update(UUID id, String displayName) {
        Channel channel = find(id);
        if (channel != null) {
            channel.setDisplayname(displayName);                   //기존에는 이것만 있음
            channel.setUpdatedAt(System.currentTimeMillis());     //팀장님이 권유해주신 시간추가
        }
        return channel;
    }

    @Override
    public boolean delete(UUID id) {
        boolean bool = channels.removeIf(channel -> id == channel.getId());
        return bool;
    }
}