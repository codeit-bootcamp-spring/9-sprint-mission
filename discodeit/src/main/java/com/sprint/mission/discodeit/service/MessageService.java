package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(MessageCreateRequest messageCreateRequest, List<BinaryContentCreateRequest> binaryContentCreateRequests);

    Message find(UUID messageId);
    // 반환 타입을 PageResponse로 변경하고 page, size 파라미터 추가
    PageResponse<Message> findAllByChannelId(UUID channelId, int page, int size);

    Message update(UUID messageId, MessageUpdateRequest request);

    void delete(UUID messageId);
}