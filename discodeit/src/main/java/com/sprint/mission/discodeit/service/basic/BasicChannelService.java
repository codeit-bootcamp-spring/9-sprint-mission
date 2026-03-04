package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.type.ChannelType;
import java.util.Comparator;
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
    public ChannelDto createPrivateChannel(PrivateChannelCreateRequest request) {
        Channel newChannel = new Channel(ChannelType.PRIVATE, "temp", "temp");
        channelRepository.save(newChannel);

        List<UUID> memberIds = request.participantIds();
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

        return this.convertToChannelResponse(newChannel);
    }

    @Override
    public ChannelDto createPublicChannel(PublicChannelCreateRequest request){
        Channel newChannel = new Channel(ChannelType.PUBLIC,
                request.name(),
                request.description()
        );
        channelRepository.save(newChannel);
        return this.convertToChannelResponse(newChannel);
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
    public ChannelDto findByID(UUID id) {
        Channel channel = channelRepository.findByID(id).orElseThrow();

        Instant lastMessageTime = Instant.EPOCH;
        List<UUID> userList = new ArrayList<>();
        if (!channel.getMessageList().isEmpty()) {
            int msgListSize = channel.getMessageList().size();
            UUID lastMessageId = channel.getMessageList().get(msgListSize - 1);
            lastMessageTime = messageRepository.findByID(lastMessageId).orElseThrow().getCreatedAt();
        }

        if (channel.getType().equals(ChannelType.PRIVATE)){
            userList = channel.getMemberList();
        }

        return new ChannelDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                userList,
                lastMessageTime
        );
    }

    @Override
    public List<ChannelDto> findAll() {
        List<Channel> channelList = channelRepository.findAll();
        return channelList.stream()
                .map(this::convertToChannelResponse)
                .toList();
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        List<Channel> channelList = channelRepository.findAll();
        return channelList.stream()
                .filter(channel -> {
                    if (channel.getType() == ChannelType.PRIVATE) {
                        return channel.getMemberList().contains(userId);
                    }
                    return true;
                })
                .map(this::convertToChannelResponse)
                .toList();
    }

    @Override
    public List<UUID> findMessagesInChannel(UUID id) {
        return channelRepository.findByID(id).orElseThrow().getMessages();
    }

    @Override
    public ChannelDto update(UUID id, ChannelUpdateRequest request) {
        Channel target = channelRepository.findByID(id).orElseThrow();

        if (target.getType() == ChannelType.PRIVATE){
            throw new IllegalStateException("채널 정보 변경 실패 (PRIVATE 채널은 수정할 수 없습니다.) | 채널ID: " + id);
        }

        target.update(request.name(),
                request.description()
        );
        channelRepository.save(target);
        return this.convertToChannelResponse(target);
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
        System.out.println(channelID);
        Channel channel = channelRepository.findByID(channelID).orElseThrow();
        channel.removeMessage(messageId);
        channelRepository.save(channel);
        return true;
    }

    private ChannelDto convertToChannelResponse(Channel channel) {
        List<UUID> userList = new ArrayList<>();

        List<UUID> messageList = channel.getMessageList();

        Instant lastMessageTime = messageRepository.findInList(messageList)
            .stream()
            .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
            .map(Message::getCreatedAt)
            .limit(1)
            .findFirst()
            .orElse(Instant.MIN);

        if (channel.getType() == ChannelType.PRIVATE) {
            userList = readStatusRepository.findAll().stream()
                    .filter(readStatus -> readStatus.getChannelId().equals(channel.getId()))
                    .map(ReadStatus::getUserId)
                    .toList();
        }

        return new ChannelDto(
            channel.getId(),
            channel.getType(),
            channel.getName(),
            channel.getDescription(),
            userList,
            lastMessageTime
        );
    }
}
