package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


@Tag(name = "Channel", description = "Channel API")
public interface ChannelApi {
  @Operation(summary = "Public Channel 등록")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "Public Channel이 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = Channel.class))
      ),
  })
  public ResponseEntity<ChannelResponse> createPublic(
      @Parameter(
          description = "Channel 생성 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      )CreatePublicChannelRequest request
  );

  @Operation(summary = "Private Channel 등록")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "Private Channel이 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = Channel.class))
      ),
  })
  public ResponseEntity<ChannelResponse> createPrivate(
      @Parameter(
          description = "Channel 생성 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      ) CreatePrivateChannelRequest request
  );

  @Operation(summary = "Channel 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Channel의 정보가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = ChannelResponse.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "Channel을 찾을 수 없음",
          content = @Content(examples = @ExampleObject("Channel with id {channelId} not found"))
      )
  })
  public ResponseEntity<ChannelResponse> update(
      @Parameter(description = "수정할 Channel ID") UUID channelId
      , @Parameter(description = "수정할 Channel의 정보") UpdateChannelRequest request);

  @Operation(summary = "Channel 삭제")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "Channel이 성공적으로 삭제됨"
      ),
      @ApiResponse(
          responseCode = "404", description = "Channel을 찾을 수 없음",
          content = @Content(examples = @ExampleObject("Channel with id {channelId} not found"))
      )
  })
  public ResponseEntity<Void> delete(@Parameter(description = "삭제할 Channel ID") UUID channelId);

  @Operation(summary = "해당 User가 접근 가능한 Channel 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Channel목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChannelResponse.class)))
      )
  })
  public ResponseEntity<List<ChannelResponse>> findByUserId(@Parameter(description = "조회할 User ID")  UUID userId);
}
