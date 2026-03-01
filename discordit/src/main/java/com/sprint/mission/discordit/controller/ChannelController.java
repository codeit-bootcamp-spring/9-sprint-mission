package com.sprint.mission.discordit.controller;

import com.sprint.mission.discordit.dto.data.ChannelDto;
import com.sprint.mission.discordit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discordit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discordit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discordit.global.ApiResult;
import com.sprint.mission.discordit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Channel API", description = "채널 서비스 전반에 관한 API를 제공")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController {

  private final ChannelService channelService;

  @Operation(summary = "public 채널 생성", description = "public 채널을 생성할 수 있습니다.")
  @ApiResponse(responseCode = "201", description = "채널 생성 성공")
  @RequestMapping(path = "public", method = RequestMethod.POST)
  public ResponseEntity<ApiResult<ChannelDto>> create(
      @RequestBody PublicChannelCreateRequest request) {
    ChannelDto createdChannel = channelService.create(request);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ApiResult.success(createdChannel));
  }

  @Operation(summary = "private 채널 생성", description = "private 채널을 생성할 수 있습니다.")
  @ApiResponse(responseCode = "200", description = "채널 생성 성공")
  @RequestMapping(path = "private", method = RequestMethod.POST)
  public ResponseEntity<ApiResult<ChannelDto>> create(
      @RequestBody PrivateChannelCreateRequest request) {
    ChannelDto createdChannel = channelService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ApiResult.success(createdChannel));
  }

  @Operation(summary = "public 채널 수정", description = "공용 채널을 수정할 수 있습니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "채널 수정 성공"),
      @ApiResponse(responseCode = "400", description = "Private 채널 수정 시도 시", content = @Content),
      @ApiResponse(responseCode = "404", description = "존재하지 않는 채널을 수정하려 할 경우", content = @Content)
  })
  @RequestMapping(path = "{channelId}", method = RequestMethod.PATCH)
  public ResponseEntity<ApiResult<ChannelDto>> update(@PathVariable UUID channelId,
      @RequestBody PublicChannelUpdateRequest request) {
    ChannelDto updatedChannel = channelService.update(channelId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ApiResult.success(updatedChannel));
  }

  @Operation(summary = "채널 삭제", description = "채널을 삭제할 수 있습니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "채널 삭제 성공"),
      @ApiResponse(responseCode = "404", description = "존재하지 않는 채널을 삭제하려 할 경우", content = @Content)
  })
  @RequestMapping(path = "{channelId}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @Operation(summary = "채널 조회", description = "전체 채널을 조회할 수 있습니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "채널 조회 성공")
  })
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {
    List<ChannelDto> channels = channelService.findAllByUserId(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(channels);
  }
}
