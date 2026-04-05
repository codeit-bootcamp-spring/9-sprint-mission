package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface MessageService {

  MessageResponse create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests);

  MessageResponse find(UUID messageId);

  List<MessageResponse> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable);

  MessageResponse update(UUID messageId, MessageUpdateRequest request);

  void delete(UUID messageId);
}
