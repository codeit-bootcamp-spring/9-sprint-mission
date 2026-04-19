package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record BinaryContentCreateRequest(
    @NotBlank(message = "파일 이름은 필수입니다.")
    String fileName,
    @NotBlank(message = "파일 형식은 필수입니다.")
    String contentType,
    @Positive
    Long size,

    byte[] bytes
) {
}
