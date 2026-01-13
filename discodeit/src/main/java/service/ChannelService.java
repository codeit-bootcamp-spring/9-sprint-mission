package service;

import entity.*;

import java.util.*;

public interface ChannelService {
    Channel Create(ChannelType type, String name);

    boolean Remove(UUID id);

    Channel findByID(UUID id);

    List<Channel> getAll();

    void updateName(UUID id, String newName);

    boolean addMember(UUID channelID, User user);

    boolean removeMember(UUID channelID, User user);

    boolean addMessage(UUID channelID, Message message);

    boolean removeMessage(UUID channelID, Message message);
}
