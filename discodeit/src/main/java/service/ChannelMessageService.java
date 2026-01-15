package service;

import entity.Channel;
import entity.ChannelMessage;
import entity.User;

import java.util.List;

public interface ChannelMessageService {
    ChannelMessage sendMessage(User sender, ChannelMessage message,Channel channel);
    List<ChannelMessage> getChannelMessages(Channel channel, User requester);
    boolean deleteMessage(User sender, ChannelMessage message,Channel channel);
}
