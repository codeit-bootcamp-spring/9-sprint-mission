package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.DTO.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.DTO.request.MessageCreateRequest;
import com.sprint.mission.discodeit.DTO.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message create(MessageCreateRequest messageCreateRequest,
                   List<BinaryContentCreateRequest> binaryContentCreateRequests);

    Message find(UUID messageId);

    List<Message> findAllByChannelId(UUID channelId);

    Message update(UUID messageId, MessageUpdateRequest request);

    void delete(UUID messageId);
}
