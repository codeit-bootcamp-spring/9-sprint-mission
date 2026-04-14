package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Channel API", description = "채널 서비스 전반에 관한 API를 제공")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController {

  private final ChannelService channelService;

  @Operation(summary = "public 채널 생성", description = "public 채널을 생성할 수 있습니다.")
  @ApiResponse(responseCode = "201", description = "채널 생성 성공")
  @PostMapping(path = "public")
  public ResponseEntity<ChannelDto> create(
      @RequestBody @Valid PublicChannelCreateRequest request) {
    log.debug("Public 채널 생성 접근: {}", request);
    ChannelDto createdChannel = channelService.create(request);

    log.info("Public 채널 생성 완료: {}", createdChannel);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdChannel);
  }

  @Operation(summary = "private 채널 생성", description = "private 채널을 생성할 수 있습니다.")
  @ApiResponse(responseCode = "201", description = "채널 생성 성공")
  @PostMapping(path = "private")
  public ResponseEntity<ChannelDto> create(
      @RequestBody @Valid PrivateChannelCreateRequest request) {
    log.debug("Private 채널 생성 시작: {}", request);
    ChannelDto createdChannel = channelService.create(request);

    log.info("Private 채널 생성 완료: {}", createdChannel);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdChannel);
  }

  @Operation(summary = "public 채널 수정", description = "공용 채널을 수정할 수 있습니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "채널 수정 성공"),
      @ApiResponse(responseCode = "400", description = "Private 채널 수정 시도 시", content = @Content),
      @ApiResponse(responseCode = "404", description = "존재하지 않는 채널을 수정하려 할 경우", content = @Content)
  })
  @PatchMapping(path = "{channelId}")
  public ResponseEntity<ChannelDto> update(@PathVariable UUID channelId,
      @RequestBody @Valid PublicChannelUpdateRequest request) {
    log.debug("Channel 수정 접근: {}", channelId);
    ChannelDto updatedChannel = channelService.update(channelId, request);
    log.info("Channel 수정 완료: {}", updatedChannel);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedChannel);
  }

  @Operation(summary = "채널 삭제", description = "채널을 삭제할 수 있습니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "채널 삭제 성공"),
      @ApiResponse(responseCode = "404", description = "존재하지 않는 채널을 삭제하려 할 경우", content = @Content)
  })
  @DeleteMapping(path = "{channelId}")
  public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
    log.debug("채널 삭제 접근: {}", channelId);
    channelService.delete(channelId);

    log.info("채널 삭제 완료: {}", channelId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @Operation(summary = "채널 조회", description = "전체 채널을 조회할 수 있습니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "채널 조회 성공")
  })
  @GetMapping
  public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {
    List<ChannelDto> channels = channelService.findByUserId(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(channels);
  }
}
