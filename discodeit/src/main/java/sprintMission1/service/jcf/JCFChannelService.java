package sprintMission1.service.jcf;

import sprintMission1.entity.Channel;
import sprintMission1.entity.User;
import sprintMission1.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> channels;

    private void printChannel(Channel channel) {
        System.out.println("--------" + channel + "--------");
        System.out.println("UID: " + channel.getId());
        System.out.println("이름: " + channel.getChannelName());
        System.out.println("소유자: " + channel.getOwner().getUserName());
        System.out.println("생성일자: " + channel.getCreatedAt());
        System.out.println("수정일자: " + channel.getUpdatedAt());
        System.out.println("--------" + channel + "--------");
    }
    public JCFChannelService() {
        this.channels = new HashMap<>();
    }

    public Channel create(User owner, String channelName) {
        Channel channel = new Channel(channelName, owner);
        channels.put(channel.getId(), channel);
        return channel;
    }

    public void read(UUID channelId) {
        Channel channel = channels.get(channelId);
        if (channel != null) {
            printChannel(channel);
        } else {
            System.out.println("채널이 존재하지 않습니다.");
        }
    }

    public void readAll() {
        for (Channel channel : channels.values()) {
            printChannel(channel);
        }
    }

    public void update(UUID channelId, String channelName) {
        Channel channel = channels.get(channelId);
        if (channel != null) {
            channel.update(channelName);
        } else {
            System.out.println("채널이 존재하지 않습니다.");
        }
    }

    public void delete(UUID channelId) {
        channels.remove(channelId);
    }
}
