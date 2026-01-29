package com.sprint.mission.discodeit.service.Basic;

import com.sprint.mission.discodeit.DTO.ChannelService.CreatePrivateChRequest;
import com.sprint.mission.discodeit.DTO.ChannelService.CreatePublicChRequest;
import com.sprint.mission.discodeit.DTO.ChannelService.FindChannelResponse;
import com.sprint.mission.discodeit.DTO.ChannelService.UpdateChannelRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.stream;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    //private final ReadStatusRepository readStatusRepository;

    @Override
    public Channel createPrivateChannel(CreatePrivateChRequest createPrivateChRequest) {
        Channel newChannel = new Channel(ChannelType.PRIVATE, "temp", "temp");
        channelRepository.save(newChannel);

        List<User> members = createPrivateChRequest.memberList();

        List<ReadStatus> readStatusList = members.stream().map(
                user->{
                    return new ReadStatus(user.getId(), newChannel.getId());
                }
        ).toList();

        return newChannel;
    }

    @Override
    public Channel createPublicChannel(CreatePublicChRequest createPublicChRequest){
        Channel newChannel = new Channel(ChannelType.PUBLIC,
                createPublicChRequest.name(),
                createPublicChRequest.description()
        );
        channelRepository.save(newChannel);
        return newChannel;
    }

    @Override
    public void remove(UUID id) {
        Channel channel = channelRepository.findByID(id);
        List<UUID> msgIdList = new ArrayList<>(channel.getMessageList());
        for(UUID msgId : msgIdList){
            messageRepository.remove(msgId);
        }

        // ReadStatus 추가해야해는데 레포지토리 구현체가 없음

        channelRepository.remove(id);
    }

    @Override
    public FindChannelResponse findByID(UUID id) {
        Channel channel = channelRepository.findByID(id);

        // 최근 메시지
        List<UUID> userList = null;
        Instant lastMessageTime = null;
        if (!channel.getMessageList().isEmpty()) {
            int msgListSize = channel.getMessageList().size();
            UUID lastMessageId = channel.getMessageList().get(msgListSize - 1);
            lastMessageTime = messageRepository.findByID(lastMessageId).getCreatedAt();
        }

        if (channel.getType().equals(ChannelType.PRIVATE)){
            userList = channel.getMemberList();
        }

        return new FindChannelResponse(
                channel.getId(),
                channel.getName(),
                channel.getDescription(),
                lastMessageTime,
                userList
        );
    }

    @Override
    public List<FindChannelResponse> findAllByUserId(UUID userId) {
        List<Channel> channelList = channelRepository.findAll();

        return channelList.stream()
                .filter(channel -> {
                    if (channel.getType() == ChannelType.PRIVATE) {
                        return channel.getMemberList().contains(userId);
                    }
                    return true;
                })
                .map(channel -> {
                    Instant lastMessageTime = null;
                    List<UUID> userList = null;

                    if (!channel.getMessageList().isEmpty()) {
                        int size = channel.getMessageList().size();
                        UUID lastMessageId = channel.getMessageList().get(size - 1);
                        lastMessageTime = messageRepository
                                .findByID(lastMessageId)
                                .getCreatedAt();
                    }

                    if (channel.getType() == ChannelType.PRIVATE) {
                        userList = channel.getMemberList();
                    }

                    return new FindChannelResponse(
                            channel.getId(),
                            channel.getName(),
                            channel.getDescription(),
                            lastMessageTime,
                            userList
                    );
                })
                .toList();
    }

    @Override
    public Channel update(UpdateChannelRequest updateChannelRequest) {
        UUID id = updateChannelRequest.id();
        Channel target = channelRepository.findByID(id);

        if (target.getType() == ChannelType.PRIVATE){
            throw new IllegalStateException("채널 정보 변경 실패 (PRIVATE 채널은 수정할 수 없습니다.) | 채널ID: " + id);
        }

        target.update(updateChannelRequest.name(),
                updateChannelRequest.description()
        );
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
