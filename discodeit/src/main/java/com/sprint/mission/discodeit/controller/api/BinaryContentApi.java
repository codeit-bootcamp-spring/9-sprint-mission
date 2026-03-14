package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

@Tag(name = "BinaryContent", description = "BinaryContent API")
public interface BinaryContentApi {

    @Operation(summary = "바이너리 컨텐츠 생성", description = "Base64 등 바이너리 데이터를 저장합니다. (POST /binary-contents)")
    BinaryContent create(BinaryContentCreateRequest request);

    @Operation(summary = "바이너리 컨텐츠 단건 조회", description = "binaryContentId로 단건 조회합니다. (GET /binary-contents/{binaryContentId})")
    BinaryContent find(UUID binaryContentId);
}