package sprintMission1.service;

import sprintMission1.entity.Channel;
import sprintMission1.entity.User;

import java.util.UUID;

public interface ChannelService {
    Channel create(User owner, String channelName);
    void read(UUID channelId);
    void readAll();
    void update(UUID id, String channelName);
    void delete(UUID id);
}
