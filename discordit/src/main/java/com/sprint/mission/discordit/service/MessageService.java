package com.sprint.mission.discordit.service;

import com.sprint.mission.discordit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discordit.dto.request.MessageCreateRequest;
import com.sprint.mission.discordit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discordit.entity.Message;

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
