package com.sprint.mission.discodeit.service.Basic;

import com.sprint.mission.discodeit.DTO.ChannelService.Request.CreatePrivateChRequest;
import com.sprint.mission.discodeit.DTO.ChannelService.Request.CreatePublicChRequest;
import com.sprint.mission.discodeit.DTO.ChannelService.Response.FindChannelResponse;
import com.sprint.mission.discodeit.DTO.ChannelService.Request.UpdateChannelRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public Channel createPrivateChannel(CreatePrivateChRequest request) {
        Channel newChannel = new Channel(ChannelType.PRIVATE, "temp", "temp");
        channelRepository.save(newChannel);

        List<UUID> memberIds = request.memberList();
        memberIds.forEach(memberId->{
            this.addMember(newChannel.getId(), memberId);
        });
        List<ReadStatus> readStatusList = memberIds.stream().map(
                memberId->{
                    return new ReadStatus(memberId, newChannel.getId());
                }
        ).toList();

        for(ReadStatus readStatus : readStatusList){
            readStatusRepository.save(readStatus);
        }

        return newChannel;
    }

    @Override
    public Channel createPublicChannel(CreatePublicChRequest request){
        Channel newChannel = new Channel(ChannelType.PUBLIC,
                request.name(),
                request.description()
        );
        channelRepository.save(newChannel);
        return newChannel;
    }

    @Override
    public void remove(UUID id) {
        Channel channel = channelRepository.findByID(id).orElseThrow();
        List<UUID> msgIdList = new ArrayList<>(channel.getMessageList());
        List<UUID> readStatusList = readStatusRepository.findByChannelID(id).stream()
                .map(ReadStatus::getId)
                .toList();
        for(UUID msgId : msgIdList){
            messageRepository.remove(msgId);
        }

        for (UUID readStatusId : readStatusList){
            readStatusRepository.remove(readStatusId);
        }

        channelRepository.remove(id);
    }

    @Override
    public FindChannelResponse findByID(UUID id) {
        Channel channel = channelRepository.findByID(id).orElseThrow();

        // 최근 메시지
        List<UUID> userList = null;
        Instant lastMessageTime = null;
        if (!channel.getMessageList().isEmpty()) {
            int msgListSize = channel.getMessageList().size();
            UUID lastMessageId = channel.getMessageList().get(msgListSize - 1);
            lastMessageTime = messageRepository.findByID(lastMessageId).orElseThrow().getCreatedAt();
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
    public List<FindChannelResponse> findAll() {
        List<Channel> channelList = channelRepository.findAll();
        return channelList.stream()
                .map(this::convertToFindChannelResponse)
                .toList();
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
                .map(this::convertToFindChannelResponse)
                .toList();
    }

    @Override
    public Channel update(UpdateChannelRequest request) {
        UUID id = request.id();
        Channel target = channelRepository.findByID(id).orElseThrow();

        if (target.getType() == ChannelType.PRIVATE){
            throw new IllegalStateException("채널 정보 변경 실패 (PRIVATE 채널은 수정할 수 없습니다.) | 채널ID: " + id);
        }

        target.update(request.name(),
                request.description()
        );
        channelRepository.save(target);
        return target;
    }


    @Override
    public boolean addMember(UUID channelID, UUID userId) {
        Channel channel = channelRepository.findByID(channelID).orElseThrow();
        channel.addMember(userId);
        channelRepository.save(channel);
        return true;
    }

    @Override
    public boolean removeMember(UUID channelID, UUID userId) {
        Channel channel = channelRepository.findByID(channelID).orElseThrow();
        channel.removeMember(userId);
        channelRepository.save(channel);
        return true;
    }

    @Override
    public boolean addMessage(UUID channelID, UUID messageId) {
        Channel channel = channelRepository.findByID(channelID).orElseThrow();
        channel.addMessage(messageId);
        channelRepository.save(channel);
        return true;
    }

    @Override
    public boolean removeMessage(UUID channelID, UUID messageId) {
        Channel channel = channelRepository.findByID(channelID).orElseThrow();
        channel.removeMessage(messageId);
        channelRepository.save(channel);
        return true;
    }

    private FindChannelResponse convertToFindChannelResponse(Channel channel) {
        Instant lastMessageTime = null;
        List<UUID> userList = null;

        List<UUID> messageList = messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channel.getId()))
                .map(BaseEntity::getId)
                .toList();

        if (!messageList.isEmpty()) {
            int size = channel.getMessageList().size();
            UUID lastMessageId = channel.getMessageList().get(size - 1);
            lastMessageTime = messageRepository
                    .findByID(lastMessageId).orElseThrow()
                    .getCreatedAt();
        }

        if (channel.getType() == ChannelType.PRIVATE) {
            userList = readStatusRepository.findAll().stream()
                    .filter(readStatus -> readStatus.getChannelId().equals(channel.getId()))
                    .map(ReadStatus::getUserId)
                    .toList();
        }

        return new FindChannelResponse(
                channel.getId(),
                channel.getName(),
                channel.getDescription(),
                lastMessageTime,
                userList
        );
    }
}
