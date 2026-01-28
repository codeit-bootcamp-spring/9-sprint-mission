package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    MessageDto.Response send(MessageDto.CreateRequest request);
    Optional<MessageDto.Response> findById(UUID id);
    List<MessageDto.Response> findAllByChannelId(UUID channelId);
    boolean delete(UUID id);
}