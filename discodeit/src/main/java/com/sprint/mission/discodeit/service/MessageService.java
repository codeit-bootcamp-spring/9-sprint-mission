package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;

import java.util.*;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {
    MessageDto create(MessageCreateRequest request, List<BinaryContentCreateRequest> attachments);

    void delete(UUID id);

    MessageDto findByID(UUID id);

    List<MessageDto> findAllByChannelId(UUID channelId);

    MessageDto updateContent(UUID id, String newContent);

}
