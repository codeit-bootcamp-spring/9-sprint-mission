package com.sprint.mission.discodeit.service;
import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.util.List;
import java.util.UUID;

public interface MessageService {
  MessageDto create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests);
  MessageDto find(UUID messageId);
  List<MessageDto> findAllByChannelId(UUID channelId);
  MessageDto update(UUID messageId, MessageUpdateRequest request);
  void delete(UUID messageId);
  PageResponse<MessageDto> findAllByChannelId(UUID channelId, int page);
}