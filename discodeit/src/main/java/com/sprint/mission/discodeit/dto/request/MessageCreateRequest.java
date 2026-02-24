package com.sprint.mission.discodeit.dto.request;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public record MessageCreateRequest(
    String content,
    UUID channelId,
    UUID authorId,
    List<MultipartFile> attachments
) {

}
