package com.sprint.mission.discodeit.dto.request;

// bytes는 스토리지 저장을 위해 유지하되,
// DB 엔티티로 변환될 때는 메타데이터(fileName, size, contentType)만 사용됩니다.
public record BinaryContentCreateRequest(
    byte[] bytes,
    String fileName,
    String contentType,
    Long size
) {

}