package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record FileInfoResponse(
        UUID fileId,
        String fileName,
        String fileUrl
) {}

// 저장된 파일 고유 식별 번호, 사용자가 올린 실제 파일 이름, 파일 다운로드하거나 볼 수 있는 인터넷주소