package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequest(
        String content,
        UUID channelId,
        UUID authorId,
        List<UUID> attachmentIds
) {}

// 메세지 실제 내용, 메세지에 적힐 채널의 고유 번호, 메세지 쓰는 사람의 고유번호, 메세지에 첨부된 파일들의 id리스트