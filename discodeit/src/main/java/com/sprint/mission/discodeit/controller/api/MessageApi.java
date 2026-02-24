package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "Message API")
public interface MessageApi {

    @Operation(summary = "메시지 전송", description = "특정 채널에 메시지를 생성합니다. (POST /channels/{channelId}/messages)")
    Message createMessage(UUID channelId, MessageCreateRequest request);

    @Operation(summary = "메시지 수정", description = "메시지 내용을 수정합니다. (PUT /messages/{messageId})")
    Message updateMessage(UUID messageId, MessageUpdateRequest request);

    @Operation(summary = "메시지 삭제", description = "메시지를 삭제합니다. (DELETE /messages/{messageId})")
    void deleteMessage(UUID messageId);

    @Operation(summary = "채널 메시지 목록 조회", description = "특정 채널의 메시지 목록을 조회합니다. (GET /channels/{channelId}/messages)")
    List<Message> getMessagesByChannel(UUID channelId);
}
