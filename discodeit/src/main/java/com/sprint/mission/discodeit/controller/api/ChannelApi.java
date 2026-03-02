package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Channel", description = "Channel API")
public interface ChannelApi {

  @Operation(summary = "공개 채널 생성")
  @PostMapping("/api/channels/public")
  ResponseEntity<ChannelDto> createPublicChannel(
      @RequestBody PublicChannelCreateRequest request
  );

  @Operation(summary = "비공개 채널 생성")
  @PostMapping("/api/channels/private")
  ResponseEntity<ChannelDto> createPrivateChannel(
      @RequestBody PrivateChannelCreateRequest request
  );

  @Operation(summary = "전체 채널 조회")
  @GetMapping
  ResponseEntity<List<ChannelDto>> findAll(
      @RequestParam("userId") UUID userId
  );
}
