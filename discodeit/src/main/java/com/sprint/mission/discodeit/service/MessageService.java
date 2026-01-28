package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message create(String content, UUID channelId, UUID authorId);

    Message findById(UUID id);

    List<Message> findAll();

    List<Message> findByChannelId(UUID channelId);

    boolean update(UUID id, String content);

    boolean delete(UUID id);

    // ===== 추가 요구사항 =====
    MessageResponse create(MessageCreateRequest request);
    MessageResponse update(MessageUpdateRequest request);
    boolean deleteDto(UUID messageId);

    MessageResponse findDtoById(UUID messageId);
    List<MessageResponse> findAllDtoByChannelId(UUID channelId);
}

