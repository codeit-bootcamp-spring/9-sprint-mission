package service.jcf;

import entity.Channel;
import entity.ChannelMessage;
import entity.Message;
import entity.User;
import service.ChannelMessageService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JCFChannelMessageService implements ChannelMessageService {
    private final Map<Channel, List<ChannelMessage>> channelMessageMap=new HashMap<>();


    @Override
    public ChannelMessage sendMessage(User sender, ChannelMessage message,Channel channel) {
                List<ChannelMessage> messages= channelMessageMap.computeIfAbsent(channel,key->new ArrayList<>());
                messages.add(message);
                return message;
    }

    @Override
    public List<ChannelMessage> getChannelMessages(Channel channel, User requester) {
            List<ChannelMessage> list=channelMessageMap.getOrDefault(channel,new ArrayList<>());
            return list;
    }

    @Override
    public boolean deleteMessage(User sender, ChannelMessage message,Channel channel) {
        List<ChannelMessage> messages = channelMessageMap.get(channel);
        if(messages==null)return false;
        return messages.remove(message);


    }
}
