//package com.sprint.mission.discodeit.service.jcf;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.ChannelType;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.service.ChannelService;
//
//import java.util.*;
//
//public class JCFChannelService implements ChannelService {
//
//    private final Map<UUID, Channel> channelMap;
//
//    public JCFChannelService(){
//        channelMap = new HashMap<>();
//    }
//
//    @Override
//    public Channel create(ChannelType type, String name) {
//        Channel newChannel = new Channel(type, name);
//        channelMap.put(newChannel.getId(), newChannel);
//        return newChannel;
//    }
//
//    @Override
//    public void remove(UUID id){
//        Channel removedChannel = channelMap.remove(id);
//        if (removedChannel == null){
//            throw new IllegalStateException("채널 삭제 실패 (해당 채널이 존재하지 않음) | 채널ID: " + id);
//        }
//    }
//
//    @Override
//    public Channel findByID(UUID id) {
//        return channelMap.get(id);
//    }
//
//    @Override
//    public List<Channel> getAll() {
//        return new ArrayList<>(channelMap.values());
//    }
//
//    @Override
//    public Channel updateName(UUID id, String newName) {
//        Channel channel = channelMap.get(id);
//        if (channel == null){
//            throw new IllegalStateException("채널 이름 변경 실패 (해당 채널이 존재하지 않음) | 채널ID: " + id);
//        }
//        channel.updateName(newName);
//        return channel;
//    }
//
//    @Override
//    public boolean addMember(UUID channelID, User user) {
//        Channel channel = channelMap.get(channelID);
//        if (channel == null){
//            throw new IllegalStateException("채널에 멤버 추가 실패 (해당 채널이 존재하지 않음) | 채널ID: " + channelID);
//        }
//        return channel.addMember(user.getId());
//    }
//
//    @Override
//    public boolean removeMember(UUID channelID, User user) {
//        Channel channel = channelMap.get(channelID);
//        if (channel == null){
//            throw new IllegalStateException("채널에 멤버 제거 실패 (해당 채널이 존재하지 않음) | 채널ID: " + channelID);
//        }
//        return channel.removeMember(user.getId());
//    }
//
//    @Override
//    public boolean addMessage(UUID channelID, Message message) {
//        Channel channel = channelMap.get(channelID);
//        if (channel == null){
//            throw new IllegalStateException("채널에 메시지 추가 실패 (해당 채널이 존재하지 않음) | 채널ID: " + channelID);
//        }
//        return channel.addMessage(message.getId());
//    }
//
//    @Override
//    public boolean removeMessage(UUID channelID, Message message) {
//        Channel channel = channelMap.get(channelID);
//        if (channel == null){
//           return false;
//        }
//        return channel.removeMessage(message.getId());
//    }
//}
