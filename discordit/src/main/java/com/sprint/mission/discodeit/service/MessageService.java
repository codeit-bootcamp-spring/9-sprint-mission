package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.RequestCreateMessageDto;
import com.sprint.mission.discodeit.dto.response.ResponseMessageDto;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    ResponseMessageDto create(RequestCreateMessageDto requestDto);

    ResponseMessageDto update(UUID userId, UUID messageId, String content);

    List<String> findAllForSender(UUID userId);

    List<ResponseMessageDto> findAllInChannel(UUID channelId);

    boolean delete(UUID userId, UUID messageId);

    void deleteAll(UUID channelId);

    String lastMessageTime(UUID channelId);
}
