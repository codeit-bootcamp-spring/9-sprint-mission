package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
    @NotNull(message = "참여자 목록이 누락되었습니다.")
    @NotEmpty(message = "비공개 채널에는 최소 1명 이상의 참여자가 필요합니다.")
    List<UUID> participantIds
) {
}