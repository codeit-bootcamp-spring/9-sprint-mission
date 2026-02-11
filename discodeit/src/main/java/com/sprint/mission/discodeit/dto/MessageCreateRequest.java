package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequest(
        String content,
        UUID channelId,
        UUID authorId,
        List<FileUploadRequest> attachments // 첨부파일 여러 개 (선택)
) {}
