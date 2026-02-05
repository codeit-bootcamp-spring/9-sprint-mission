package com.sprint.mission.discodeit.dto.message;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequest(
        UUID channelId,
        UUID userId,
        Params params
) {
    public record Params(
            String content,
            List<AttachmentParams> attachments
    ) {
    }

    /**
     * 첨부파일 업로드 파라미터
     * - bytes/contentType/filename은 파일 저장 서비스에서 사용
     */
    public record AttachmentParams(
            byte[] bytes,
            String contentType,
            String filename
    ) {
    }
}
