package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BinaryContentCreateRequest(
    @NotEmpty(message = "파일 데이터는 비어있을 수 없습니다.")
    byte[] bytes,

    @NotBlank(message = "파일 이름은 필수입니다.")
    String fileName,

    @NotBlank(message = "컨텐츠 타입은 필수입니다.")
    String contentType,

    @NotNull(message = "파일 크기는 필수입니다.")
    @Positive(message = "파일 크기는 0보다 커야 합니다.")
    Long size
) {

}