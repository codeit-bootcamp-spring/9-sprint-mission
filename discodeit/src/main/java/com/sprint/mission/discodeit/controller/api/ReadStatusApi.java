package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "ReadStatus API")
public interface ReadStatusApi {

    @Operation(summary = "메시지 수신 정보 생성", description = "read-statuses를 생성합니다.")
    ReadStatus createReadStatus(@RequestBody ReadStatusCreateRequest request);

    @Operation(summary = "메시지 수신 정보 수정", description = "readStatusId로 수신 정보를 수정합니다.")
    ReadStatus updateReadStatus(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    );

    @Operation(summary = "특정 사용자의 메시지 수신 정보 조회", description = "userId에 해당하는 read-statuses 목록을 조회합니다.")
    List<ReadStatus> getReadStatusesByUser(@PathVariable UUID userId);
}