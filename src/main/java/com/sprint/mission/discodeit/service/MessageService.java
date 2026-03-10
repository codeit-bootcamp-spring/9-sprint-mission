package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.PageResponse;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.apache.logging.log4j.CloseableThreadContext.Instance;

public interface MessageService {

  MessageDto create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests);
  MessageDto find(UUID messageId);
  PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor);
  MessageDto update(UUID messageId, MessageUpdateRequest request);
  void delete(UUID messageId);
}