package service.basic;

import entity.Channel;
import exception.NotFoundException;
import repository.ChannelRepository;
import service.ChannelService;

import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel create(String name, UUID ownerId) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("channel name must not be blank");
        }
        if (ownerId == null) {
            throw new IllegalArgumentException("ownerId must not be null");
        }

        Channel channel = new Channel(name, ownerId);
        return channelRepository.save(channel);
    }

    @Override
    public Channel update(UUID channelId, String name) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Channel not found. id=" + channelId));

        if (name != null) {
            if (name.isBlank()) throw new IllegalArgumentException("channel name must not be blank");

           /// 채널명 예외
            // if (!name.equals(channel.getName()) && channelRepository.existsByName(name)) {
            //     throw new IllegalArgumentException("Channel name already exists: " + name);
            // }
        }

        channel.update(name);
        return channelRepository.save(channel);
    }

    @Override
    public Channel findById(UUID channelId) {
        return channelRepository.findById(channelId).orElse(null);
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NotFoundException("Channel not found. id=" + channelId);
        }
        channelRepository.deleteById(channelId);
    }

    @Override
    public boolean existsById(UUID channelId) {
        return channelRepository.existsById(channelId);
    }
}