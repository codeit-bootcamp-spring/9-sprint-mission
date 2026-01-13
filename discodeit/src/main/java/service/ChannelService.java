package service;

import entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

        //boolean addChannel(Channel channel);

        Channel create(String frame, String channelName, String detail);


        Channel find(UUID id);

        void delete (UUID id);


        List<Channel> findAll();

        Channel update(UUID id, String channelName, String detail);
}
