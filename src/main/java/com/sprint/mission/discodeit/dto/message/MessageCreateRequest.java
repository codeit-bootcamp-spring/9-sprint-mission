package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class MessageCreateRequest {
    private String content;
    private UUID userId;
    private UUID channelId;
    private List<BinaryContentCreateRequest> attachments;
}
