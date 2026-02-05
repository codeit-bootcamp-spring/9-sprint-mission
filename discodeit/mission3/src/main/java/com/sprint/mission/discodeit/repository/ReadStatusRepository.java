package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.DTO.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository  {
    ReadStatus create(UUID userId, UUID channelId);
    ReadStatus find(UUID id);
    List<ReadStatus> findAllByUserId(UUID userId);
    List<ReadStatus> findAllByChannelId(UUID channelID);
    List<ReadStatus> findAll();
    ReadStatus update(ReadStatusDto.UpdateDto updateDto);
    void delete(UUID Id);

}
