package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageService {

  MessageDto send(MessageCreateRequest request,
      List<BinaryContentCreateRequest> attachmentRequests);

  PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, int size);

  MessageDto update(UUID messageId, MessageUpdateRequest request);

  void delete(UUID messageId);

  MessageDto findById(UUID id);
}