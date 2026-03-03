package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "Channel API")
public interface ChannelApi {

  @Operation(summary = "Public Channel 생성")
  @ApiResponses({
      @ApiResponse(
          responseCode = "201",
          description = "Public Channel 생성 성공",
          content = @Content(schema = @Schema(implementation = ChannelDto.class))
      )
  })
  ResponseEntity<ChannelDto> createPublicChannel(
      @Parameter(description = "Public Channel 생성 요청")
      PublicChannelCreateRequest request
  );

  @Operation(summary = "Private Channel 생성")
  @ApiResponses({
      @ApiResponse(
          responseCode = "201",
          description = "Private Channel 생성 성공",
          content = @Content(schema = @Schema(implementation = ChannelDto.class))
      )
  })
  ResponseEntity<ChannelDto> createPrivateChannel(
      @Parameter(description = "Private Channel 생성 요청")
      PrivateChannelCreateRequest request
  );

  @Operation(summary = "Channel 목록 조회")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Channel 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChannelDto.class)))
      )
  })
  ResponseEntity<List<ChannelDto>> findAll(
      @Parameter(description = "User ID")
      UUID userId
  );

  @Operation(summary = "Channel 수정")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Channel 수정 성공",
          content = @Content(schema = @Schema(implementation = ChannelDto.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Channel not found",
          content = @Content(examples = @ExampleObject("Channel not found"))
      )
  })
  ResponseEntity<ChannelDto> update(
      @Parameter(description = "Channel ID")
      UUID channelId,

      @Parameter(description = "Channel 수정 요청")
      PublicChannelUpdateRequest request
  );

  @Operation(summary = "Channel 삭제")
  @ApiResponses({
      @ApiResponse(
          responseCode = "204",
          description = "Channel 삭제 성공"
      )
  })
  ResponseEntity<Void> delete(
      @Parameter(description = "Channel ID")
      UUID channelId
  );
}