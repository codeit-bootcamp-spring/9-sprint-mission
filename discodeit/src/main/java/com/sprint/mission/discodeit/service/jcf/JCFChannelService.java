package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.DTO.ChannelDTO;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.Exception.ChannelCrudException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
        private final List<Channel> data = new ArrayList<>();

//    @Override
//    public Channel createChannel(ChannelType type, String channelName, String description) {
//        Channel channel = new Channel(channelName, description);
//        data.add(channel);
//        return channel;
//    }

    @Override
    public Channel createPublicChannel(ChannelDTO.CreatePublicChannelDTO dto) {
       Channel channel = new Channel(dto.channelName(), dto.description(), ChannelType.PUBLIC);
       data.add(channel);
       return channel;
    }

    @Override
    public Channel createPrivateChannel(ChannelDTO.CreatePrivateChannelDTO dto) {
        if (data.stream().anyMatch(c -> c.getChannelId().equals(dto.channelId()))) {
            throw new IllegalArgumentException("이미 존재하는 채널입니다: " + dto.channelId());
        }
        for (UUID userId : dto.channelMembersIds()) {
            if (!data.stream().anyMatch(c -> c.getUserId().equals(userId))) {
                throw new NoSuchElementException("해당 채널에 속하지 않은 아이디 입니다: " + userId);
            }
        }
        Channel channel = new Channel(ChannelType.PRIVATE);
        data.add(channel);
        return channel;
    }

    @Override
    public Channel findByChannelId(UUID channelId) {
        for (Channel channel : data) {
            if (channel.getChannelId().equals(channelId)) {
                return channel;
            }
        }
        throw new RuntimeException("Channel not found");
    }

    @Override
    public List<Channel> findAllChannel() {
        if (data.isEmpty()) {
            System.out.println("만들어진 채널이 없습니다.");
        }
        return data;
    }


    @Override
    public Channel updateChannel(UUID channelId, String channelName, ChannelType type, String description) {

        try {
            Channel channel = this.data.stream().filter
                    (c -> c.getChannelId().equals(channelId)).findFirst().orElse(null);

            if (channel == null) {
                throw new RuntimeException("해당 Id를 가진 채널이 없습니다.");
            }

            boolean updated = false;

            if (channelName != null) {
                channel.updateChannel(channelName);
                updated = true;
            }

            if (description != null) {
                channel.updateDescription(description);
                updated = true;
            }

            if (!updated) {
                throw new RuntimeException("수정할 값이 없습니다");
            }

            return channel;
        }catch (ChannelCrudException e){
            System.out.println("Channel에서 다음과 같은 에러가 발생했습니다: " + e.getMessage());
            throw e;
        }

    }


    @Override
    public boolean deleteChannel(UUID channelId) {
        Channel channel = findByChannelId(channelId);
        if (channel == null) {
            System.out.println("삭제할 채널이 없습니다.");
            return false;
        }
        return data.remove(channel);
    }

}
