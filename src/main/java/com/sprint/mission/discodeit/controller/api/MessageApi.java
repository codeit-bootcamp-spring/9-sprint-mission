package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Message", description = "Message API")
public interface MessageApi {

  @Operation(summary = "메시지 생성")
  ResponseEntity<MessageDto> create(
      @RequestBody MessageCreateRequest request
  );

  @Operation(summary = "메시지 단건 조회")
  ResponseEntity<MessageDto> find(
      @PathVariable UUID messageId
  );

  @Operation(summary = "채널별 메시지 조회")
  ResponseEntity<List<MessageDto>> findByChannelId(
      @RequestParam UUID channelId
  );

  @Operation(summary = "메시지 수정")
  ResponseEntity<MessageDto> update(
      @PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest request
  );

  @Operation(summary = "메시지 삭제")
  ResponseEntity<Void> delete(
      @PathVariable UUID messageId
  );
}
