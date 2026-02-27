package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelView;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "Channel API")
@RequestMapping("/api/channels")
public interface ChannelApi {

  @Operation(summary = "공개 채널 생성")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "생성 성공",
          content = @Content(schema = @Schema(implementation = ChannelView.class))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Bad Request",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  @PostMapping(value = "/public", consumes = MediaType.APPLICATION_JSON_VALUE)
  ChannelView createPublic(
      @Parameter(description = "공개 채널 생성 요청")
      @RequestBody PublicChannelCreateRequest request
  );

  @Operation(summary = "비공개 채널 생성")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "생성 성공",
          content = @Content(schema = @Schema(implementation = ChannelView.class))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Bad Request",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  @PostMapping(value = "/private", consumes = MediaType.APPLICATION_JSON_VALUE)
  ChannelView createPrivate(
      @Parameter(description = "비공개 채널 생성 요청")
      @RequestBody PrivateChannelCreateRequest request
  );

  @Operation(summary = "채널 단건 조회")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "조회 성공",
          content = @Content(schema = @Schema(implementation = ChannelView.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Not Found",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  @GetMapping("/{channelId}")
  ChannelView findById(
      @Parameter(description = "Channel ID")
      @PathVariable UUID channelId
  );

  @Operation(summary = "공개 채널 정보 수정")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "수정 성공",
          content = @Content(schema = @Schema(implementation = ChannelView.class))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Bad Request",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Not Found",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  @PatchMapping(value = "/{channelId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  ChannelView update(
      @Parameter(description = "Channel ID")
      @PathVariable UUID channelId,
      @Parameter(description = "채널 수정 params")
      @RequestBody ChannelUpdateRequest.ChannelUpdateParams params
  );

  @Operation(summary = "채널 삭제")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "삭제 성공"),
      @ApiResponse(
          responseCode = "404",
          description = "Not Found",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  @DeleteMapping("/{channelId}")
  ResponseEntity<Void> delete(
      @Parameter(description = "Channel ID")
      @PathVariable UUID channelId
  );

  @Operation(summary = "사용자 기준 채널 목록 조회")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "조회 성공",
          content = @Content(schema = @Schema(implementation = ChannelView.class))
      )
  })
  @GetMapping
  List<ChannelView> findAllByUserId(
      @Parameter(description = "User ID")
      @RequestParam("userId") UUID userId
  );
}