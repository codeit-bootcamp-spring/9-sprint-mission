package com.sprint.mission.discodeit.dto;

public record ProfileImageRequest(
        byte[] data,
        String contentType
) {
    public ProfileImageRequest {
        if (data == null || data.length == 0) throw new IllegalArgumentException("파일 데이터는 필수입니다.");
        if (contentType == null || contentType.isBlank()) throw new IllegalArgumentException("contentType은 필수입니다.");
    }
}
