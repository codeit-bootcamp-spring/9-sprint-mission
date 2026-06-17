package service.jcf;

import entity.Channel;
import service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

    public class JCFChannelService  implements ChannelService {
    private final List<Channel> data;

    public JCFChannelService() {
        this.data = new ArrayList<>();
    }

    @Override
    public boolean addChannel(Channel channel) {
        return data.add(channel);
    }



    @Override
    public Channel getChannelById(UUID Id) {
        for (Channel channel : data) {
            if (channel.getId().equals(Id)) {
                return channel;
            }
        }
        return null;
    }

        @Override
        public Channel getChannelByName(String name) {
            return null;
        }

        @Override
    public Channel getChannelById(String channelId) {
        for (Channel ch : data) {
            if (ch.getId() != null && ch.getId().toString().equals(channelId)) {
                return ch;
            }
        }
        return null;
    }

    @Override
    public List<Channel> getallChannels() {
        return data;
    }

    @Override
    public List<Channel> searchChannels(String nameorownerId) {
        List<Channel> channels = new ArrayList<>();
        for (Channel channel : data) {
            if (channel.getChannelName().equals(nameorownerId) ||
                    (channel.getownerId() != null && channel.getownerId().equals(nameorownerId))) {
                channels.add(channel);
        }

    }
        return channels;
    }

    @Override
    public List<Channel> getChannelsByOwnerId(String ownerId) {
        List<Channel> result = new ArrayList<>();
        for (Channel channel : data) {
            if (channel.getownerId().equals(ownerId)) {
                result.add(channel);
            }
        }
        return result;
    }

    @Override
    public Channel updateChannel(String oldName,String newName, String description) {
        for (Channel channel : data) {
            if (channel.getChannelName().equals(oldName)) {
                channel.setChannelName(newName);
                channel.setDescription(description);
                channel.setUpdatedAt(System.currentTimeMillis());
                return channel;
            }
        }
        return null;
    }

    @Override
    public boolean deleteChannel(String name) {
        return data.removeIf(channel -> channel.getChannelName().equals(name));
    }

    @Override
    public Channel getChannelByownerId(String channelId) {
        return null;
    }
}
