package service;

import entity.*;

import java.util.*;

public interface ChannelService {
    Channel Create(ChannelType type, String name);

    void Remove(UUID id);

    Channel findByID(UUID id);

    List<Channel> getAll();

    void updateName(UUID id, String newName);

    void addMember(UUID channelID, User user);

    void removeMember(UUID channelID, User user);

    void addMessage(UUID channelID, Message message);

    void removeMessage(UUID channelID, Message message);
}
