package service.Basic;

import entity.Channel;
import entity.ChannelType;
import entity.Message;
import entity.User;
import repository.ChannelRepository;
import service.ChannelService;

import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository){
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel create(ChannelType type, String name) {
        Channel newChannel = new Channel(type, name);
        channelRepository.save(newChannel);
        return newChannel;
    }

    @Override
    public void remove(UUID id) {
        channelRepository.remove(id);
    }

    @Override
    public Channel findByID(UUID id) {
        return channelRepository.findByID(id);
    }

    @Override
    public List<Channel> getAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateName(UUID id, String newName) {
        Channel target = channelRepository.findByID(id);
        if (target == null){
            throw new IllegalStateException("채널 이름 변경 실패 (해당 채널이 존재하지 않음) | 채널ID: " + id);
        }
        target.updateName(newName);
        channelRepository.save(target);
        return target;
    }


    @Override
    public boolean addMember(UUID channelID, User user) {
        Channel channel = channelRepository.findByID(channelID);
        channel.addMember(user.getId());
        channelRepository.save(channel);
        return true;
    }

    @Override
    public boolean removeMember(UUID channelID, User user) {
        Channel channel = channelRepository.findByID(channelID);
        channel.removeMember(user.getId());
        channelRepository.save(channel);
        return true;
    }

    @Override
    public boolean addMessage(UUID channelID, Message message) {
        Channel channel = channelRepository.findByID(channelID);
        channel.addMessage(message.getId());
        channelRepository.save(channel);
        return true;
    }

    @Override
    public boolean removeMessage(UUID channelID, Message message) {
        Channel channel = channelRepository.findByID(channelID);
        channel.removeMessage(message.getId());
        channelRepository.save(channel);
        return true;
    }
}
