package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record BinaryContentCreateRequest(
    @NotBlank(message = "파일명은 필수입니다.")
    String fileName,

    @NotBlank(message = "컨텐츠 타입(MIME)은 필수입니다.")
    String contentType,

    @NotNull(message = "파일 데이터가 누락되었습니다.")
    @NotEmpty(message = "빈 파일은 업로드할 수 없습니다.")
    byte[] bytes
) {

}
