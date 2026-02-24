package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "Channel API")
public interface ChannelApi {

  @Operation(summary = "Channel 생성")
  @ApiResponse(
      responseCode = "201",
      description = "Channel이 성공적으로 생성됨",
      content = @Content(schema = @Schema(implementation = Channel.class))
  )
  ResponseEntity<Channel> create(
      @Parameter(description = "Channel 생성 정보")
      ChannelCreateRequest request
  );

  @Operation(summary = "Channel 단건 조회")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Channel 조회 성공",
          content = @Content(schema = @Schema(implementation = ChannelDto.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Channel을 찾을 수 없음"
      )
  })
  ResponseEntity<ChannelDto> find(UUID channelId);

  @Operation(summary = "Channel 전체 조회")
  @ApiResponse(
      responseCode = "200",
      description = "Channel 목록 조회 성공",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChannelDto.class)))
  )
  ResponseEntity<List<ChannelDto>> findAll();

  @Operation(summary = "Channel 수정")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Channel 수정 성공",
          content = @Content(schema = @Schema(implementation = ChannelDto.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Channel을 찾을 수 없음"
      )
  })
  ResponseEntity<ChannelDto> update(
      UUID channelId,
      ChannelUpdateRequest request
  );

  @Operation(summary = "Channel 삭제")
  @ApiResponses({
      @ApiResponse(
          responseCode = "204",
          description = "Channel 삭제 성공"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Channel을 찾을 수 없음"
      )
  })
  ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 Channel ID")
      UUID channelId
  );
}
