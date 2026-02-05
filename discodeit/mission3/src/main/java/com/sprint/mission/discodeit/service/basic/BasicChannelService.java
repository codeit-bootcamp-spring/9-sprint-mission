package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.Locked;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public Channel createPublicChannel(ChannelType type, ChannelDto.PublicDto publicDto) {
        return channelRepository.save(new Channel(ChannelType.PUBLIC,publicDto.name(),publicDto.description()));

    }

    @Override
    public Channel createPrivateChannel(ChannelType type,ChannelDto.PrivateDto privateDto){
        Channel channel=channelRepository.save(new Channel(ChannelType.PRIVATE));
        privateDto.userlist().stream()
                .forEach(userId->readStatusRepository.create(userId,channel.getId()));



         return channel;
    }



    @Override
    public ChannelDto.FindDto findChannel(UUID id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(()->new RuntimeException("채널이 없습니다."));
        Instant lastMessageAt = messageRepository.findAll()
                .stream()
                .filter(m->m.getChannelId().equals(id))
                .map(Message::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);

        List<UUID> userIds = List.of();
        if(channel.getType() == ChannelType.PRIVATE){
            userIds = readStatusRepository.findAll()
                    .stream()
                    .filter(c->c.getChannelId().equals(id))
                    .map(ReadStatus::getUserId)
                    .toList();
        }
        return new ChannelDto.FindDto(
                channel.getId(),
                channel.getName(),
                channel.getType(),
                lastMessageAt,
                userIds
        );



    }

    @Override
    public List<ChannelDto.FindDto> findAllByUserId(UUID userId) {
        List<Channel> allChannel = channelRepository.findAll();
        List<ReadStatus> readStatusList = readStatusRepository.findAll();
        List<Message> allMessage = messageRepository.findAll();
        return allChannel.stream()
                .filter(channel -> {
                    if(channel.getType().equals(ChannelType.PUBLIC)) return true;
                    return readStatusList.stream()
                            .anyMatch(c->c.getUserId().equals(userId)&& c.getChannelId().equals(channel.getId()));
                })
                .map(channel ->{
                    Instant lastMessageAt =allMessage
                            .stream()
                            .filter(m->m.getChannelId().equals(channel.getId()))
                            .map(Message::getCreatedAt)
                            .max(Comparator.naturalOrder())
                            .orElse(null);
                    List<UUID> userList = null;
                    if(channel.getType()==ChannelType.PRIVATE){
                        userList = readStatusList
                                .stream()
                                .filter(c->c.getChannelId().equals(channel.getId()))
                                .map(ReadStatus::getUserId)
                                .toList();
                    }
                    return new ChannelDto.FindDto(
                            channel.getId(),
                            channel.getName(),
                            channel.getType(),
                            lastMessageAt,
                            userList
                    );



                })
                .toList();
    }

    @Override
    public Channel update(ChannelType type,ChannelDto.UpdateDto updateDto) {
        if(type == ChannelType.PRIVATE){
            throw new RuntimeException("Private방은 수정하실 수 없습니다.");
        }
        Optional<Channel> optionalChannel = channelRepository.findById(updateDto.Id());
        Channel channel =
        optionalChannel.orElseThrow(()->new RuntimeException("채널이 없습니다."));
        channel.update(updateDto.newName(), updateDto.newDescription());
        channelRepository.save(channel);

    return channel;




    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }
        messageRepository.findAll().stream()
                .filter(m->m.getChannelId().equals(channelId))
                .forEach(m->messageRepository.deleteById(m.getId()));
        readStatusRepository.findAll().stream()
                .filter(r->r.getChannelId().equals(channelId))
                .forEach(r->readStatusRepository.delete(r.getId()));

        channelRepository.deleteById(channelId);

    }
}
