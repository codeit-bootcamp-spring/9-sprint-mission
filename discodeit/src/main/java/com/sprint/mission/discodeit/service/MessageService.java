package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.util.List;
import java.util.UUID;

public interface MessageService {

  MessageDto send(MessageCreateRequest request,
      List<BinaryContentCreateRequest> attachmentRequests);

  // page -> lastMessageId, size로 파라미터 변경 [cite: 2026-03-09]
  PageResponse<MessageDto> findAllByChannelId(UUID channelId, UUID lastMessageId, int size);

  MessageDto update(UUID messageId, MessageUpdateRequest request);

  void delete(UUID messageId);

  MessageDto findById(UUID id);
}