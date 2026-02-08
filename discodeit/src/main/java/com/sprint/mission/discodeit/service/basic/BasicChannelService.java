package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.ChannelDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.status.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;


//    public BasicChannelService(ChannelRepository channelRepository) {
//        this.channelRepository = channelRepository;
//    }
    @Override
    public Channel createPublicChannel(ChannelDTO.CreatePublicChannelDTO dto){
        if (channelRepository.existsById(dto.channelId())){
            throw new IllegalArgumentException("이미 존재하는 채널입니다.");
        }

        Channel channel = new Channel(
                dto.channelName(),
                dto.description(),
                ChannelType.PUBLIC);

        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel createPrivateChannel(ChannelDTO.CreatePrivateChannelDTO dto){
        if (channelRepository.existsById(dto.channelId())){
            throw new IllegalArgumentException("이미 존재하는 채널입니다.");
        }

        Channel channel = new Channel(ChannelType.PRIVATE);
        channelRepository.save(channel);

        for (UUID userId : dto.channelMembersIds()) {
            ReadStatus readStatus = ReadStatus.createForChannel(
                    userId,
                    channel.getChannelId()
            );
            readStatusRepository.save(readStatus);
        }

        return channel;
    }



//    @Override
//    public Channel createChannel(ChannelType type, String channelName, String description) {
//        Channel channel = new Channel(channelName, description, type);
//        return channelRepository.save(channel);
//    }

    @Override
    public Channel findByChannelId(UUID ChannelId) {
        return channelRepository.findByChannelId(ChannelId)
                .orElseThrow(() -> new NoSuchElementException
                        ("Channel with id " + ChannelId + " not found"));
    }

    public ChannelDTO.FindPublicChannelDTO findPublicChannel(UUID userId, UUID channelId){
        Channel channel = channelRepository.findByChannelId(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        ReadStatus readStatus = readStatusRepository.findByUserIdAndChannelId(userId, channelId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with userId " + userId + " and channelId " + channelId + " not found"));

        return new ChannelDTO.FindPublicChannelDTO(
                channel.getChannelId(),
                readStatus.getUpdatedAt()
        );
    }

    public ChannelDTO.FindPrivateChannelDTO findPrivateChannel(UUID userId, UUID channelId){
        Channel channel = channelRepository.findByChannelId(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        ReadStatus readStatus = readStatusRepository.findByUserIdAndChannelId(userId, channelId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with userId " + userId + " and channelId " + channelId + " not found"));

        return new ChannelDTO.FindPrivateChannelDTO(
                channel.getChannelId(),
                channel.getUserId(),
                readStatus.getUpdatedAt()
        );
    }

    @Override
    public List<Channel> findAllChannel() {
        return channelRepository.findAllChannel();
    }

    public Stream<ChannelDTO.FindPublicChannelDTO> findAllPublicChannel(){

        List<Channel> channels = channelRepository.findAllChannel();

        return channels.stream()
                .map(channel -> {
                    ReadStatus readStatus = readStatusRepository.findByUserIdAndChannelId(channel.getUserId(), channel.getChannelId())
                            .orElseThrow(() -> new NoSuchElementException("ReadStatus with userId " + channel.getUserId() + " and channelId " + channel.getChannelId() + " not found"));

                    return new ChannelDTO.FindPublicChannelDTO(
                            channel.getChannelId(), readStatus.getUpdatedAt());
                });
    }

    public Stream<ChannelDTO.FindPrivateChannelDTO> findAllPrivateChannel(){
        List<Channel> channels = channelRepository.findAllChannel();

        return channels.stream()
                .map(channel -> {
                    ReadStatus readStatus = readStatusRepository.findByUserIdAndChannelId(channel.getUserId(), channel.getChannelId())
                            .orElseThrow(() -> new NoSuchElementException("ReadStatus with userId " + channel.getUserId() + " and channelId " + channel.getChannelId() + " not found"));

                    return new ChannelDTO.FindPrivateChannelDTO(
                            channel.getChannelId(),
                            channel.getUserId(),
                            readStatus.getUpdatedAt());

                });
    }


    public List<Channel> findAllChannelByUserId(UUID userId){
        if (!channelRepository.existsById(userId)){
            throw new NoSuchElementException("you don't have any channel");
        }
        if (ChannelType.PRIVATE.equals(userId)){
            return channelRepository.findAllByUserId(userId);
        }
        return channelRepository.findAllChannel();
    }



    @Override
    public Channel updateChannel(UUID ChannelId, String channelName, ChannelType type, String description) {
//        Channel channel = channelRepository.findByChannelId(ChannelId)
//                .orElseThrow(() -> new NoSuchElementException("Channel with id " + ChannelId + " not found"));
//        return channelRepository.save(channel);
        if (ChannelType.PRIVATE.equals(type)) {
            throw new IllegalArgumentException("you can't change channel type to PRIVATE");
        }
        Channel channel = channelRepository.findByChannelId(ChannelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + ChannelId + " not found"));

        return channel;
    }

    @Override
    public boolean deleteChannel(UUID ChannelId) {
//        if (channelRepository.existsById(ChannelId)) {
//            throw new NoSuchElementException("Channel with id " + ChannelId + " not found");
//        }
//        channelRepository.deleteById(ChannelId);
//        return true;
        Optional<Channel> optionalChannel = channelRepository.findByChannelId(ChannelId);
        if (optionalChannel.isEmpty()) {
            return false;
        }

        Channel channel = optionalChannel.get();
        readStatusRepository.delete(ReadStatus.createForChannel(channel.getUserId(), channel.getChannelId()));

        UUID MessageId = channel.getMessageId();

        if (MessageId != null){
            messageRepository.deleteById(MessageId);
        }else{
            throw new NoSuchElementException("messageId not found");
        }
        channelRepository.deleteById(ChannelId);
        return true;
    }
}
