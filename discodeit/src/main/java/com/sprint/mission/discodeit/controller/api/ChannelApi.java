package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "Channel API")
public interface ChannelApi {

    @Operation(summary = "공개 채널 생성", description = "공개 채널을 생성합니다. (POST /channels/public)")
    Channel createPublicChannel(PublicChannelCreateRequest request);

    @Operation(summary = "비공개 채널 생성", description = "비공개 채널을 생성합니다. (POST /channels/private)")
    Channel createPrivateChannel(PrivateChannelCreateRequest request);

    @Operation(summary = "채널 정보 수정", description = "공개 채널 정보를 수정합니다. (PUT /channels/{channelId})")
    Channel updatePublicChannel(UUID channelId, PublicChannelUpdateRequest request);

    @Operation(summary = "채널 삭제", description = "채널을 삭제합니다. (DELETE /channels/{channelId})")
    void deleteChannel(UUID channelId);

    @Operation(summary = "유저 기준 채널 목록 조회", description = "특정 사용자가 볼 수 있는 채널 목록을 조회합니다. (GET /channels?userId=...)")
    List<ChannelDto> getChannelsByUser(UUID userId);

    @Operation(summary = "채널 단건 조회", description = "채널 단건 정보를 조회합니다. (GET /channels/{channelId})")
    ChannelDto getChannel(UUID channelId);
}