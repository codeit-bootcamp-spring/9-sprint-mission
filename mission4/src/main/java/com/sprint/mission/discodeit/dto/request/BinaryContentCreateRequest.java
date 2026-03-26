package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record BinaryContentCreateRequest(
    @NotBlank(message = "파일 이름은 필수입니다.")
    @Size(min = 1, max = 30, message = "파일 이름은 최소 1자 이상 최대 30자 이내로 해야합니다.")
    String fileName,
    @NotBlank(message = "파일 타입은 필수입니다.")
    @Size(min = 1, max = 30, message = "파일 타입은 최소 1자 이상 최대 30자 이내로 해야합니다.")
    String contentType,
    @NotEmpty(message = "파일 데이터는 비어있을 수 없습니다.")
    byte[] bytes
) {

}
