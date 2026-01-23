package service.basic;

import entity.Channel;
import entity.User;
import repository.ChannelRepository;
import repository.UserRepository;
import service.ChannelService;
import service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public Channel update(UUID id, String frame, String channelName, String detail) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));
        channel.update(frame, channelName, detail);
        return channelRepository.save(channel);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>();
    }

    @Override
    public Channel create(String frame, String channelName, String detail) {
        Channel channel = new Channel(frame, channelName, detail);
        return channelRepository.save(channel);
    }

    @Override
    public Channel find(UUID id) {
        return null;
    }

}