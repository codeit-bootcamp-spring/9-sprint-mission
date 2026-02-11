package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus create(ReadStatusDto.CreateDto createDto) {
        if(!channelRepository.existsById(createDto.channelId())||!userRepository.existsById(createDto.userId())){
            throw new NoSuchElementException("채널이나 유저가 존재하지 않습니다.");
        }
        List<ReadStatus> readStatusList = readStatusRepository.findAllByUserId(createDto.userId());
        boolean isExists = readStatusList.stream()
                .anyMatch(rs->rs.getChannelId().equals(createDto.channelId()));
        if(isExists){
            throw new IllegalArgumentException("유저는 이 체널에 대한 readstatus상태가 존재함");
        }
        return readStatusRepository.create(createDto.userId(),createDto.channelId());
    }

    @Override
    public ReadStatus find(UUID id) {
        return readStatusRepository.find(id);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
       return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public ReadStatus update(ReadStatusDto.UpdateDto updateDto) {
        if(readStatusRepository.find(updateDto.Id())==null){
            throw new NoSuchElementException("업데이트할 대상이 없습니다.");
        }
        return readStatusRepository.update(updateDto);
    }

    @Override
    public void delete(UUID id) {
        if(readStatusRepository.find(id)==null){
            throw new NoSuchElementException("삭제할 대상이 없습니다.");
        }
        readStatusRepository.delete(id);
    }
}
