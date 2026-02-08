package com.sprint.mission.discodeit.service.status;

import com.sprint.mission.discodeit.DTO.ReadStatusDTO;
import com.sprint.mission.discodeit.entity.status.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public ReadStatus createReadStatus(ReadStatusDTO.CreateReadStatusDTO dto){
        if (!userRepository.existsById(dto.userId())){
            throw new NoSuchElementException("can't find user with id " + dto.userId());
        }
        if (!channelRepository.existsById(dto.channelId())){
            throw new NoSuchElementException("can't find channel with id " + dto.channelId());
        }
        if (userRepository.equals(dto.userId())){
            throw new IllegalArgumentException("you can't mark yourself as read");
        }
        if (channelRepository.equals(dto.channelId())){
            throw new IllegalArgumentException("you can't mark your channel as read");
        }
        ReadStatus readStatus = new ReadStatus(
                dto.userId(),
                dto.channelId(),
                dto.messageId()
        );
        readStatusRepository.save(readStatus);
        return readStatus;
    }

    public ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId){
        return readStatusRepository.findByUserIdAndChannelId(userId,channelId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with userId " + userId + " and channelId " + channelId + " not found"));
    }

    public ReadStatus findAllByUserId(UUID userId){
        return readStatusRepository.findAllByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with userId " + userId + " not found"));
    }

    public ReadStatus updateReadStatus(ReadStatusDTO.UpdateReadStatusDTO dto ){
        if (!readStatusRepository.existsById(dto.messageId())){
            throw new NoSuchElementException("can't find message with id " + dto.messageId());
        }
        if(readStatusRepository.existsById(dto.messageId())){
            return new ReadStatus(dto.userId(), dto.channelId(), dto.messageId());
        }
        return null;
    }


    public void deleteReadStatus(UUID userId, UUID channelId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("can't find user with id " + userId);
        }
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("can't find channel with id " + channelId);
        }
        if (userRepository.equals(userId)) {
            throw new IllegalArgumentException("you can't mark yourself as read");
        }
        if (channelRepository.equals(channelId)) {
            throw new IllegalArgumentException("you can't mark your channel as read");
        }

        readStatusRepository.delete(ReadStatus.createForChannel(userId, channelId));
    }

}
