package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "Channel API")
public interface ChannelApi {

  @Operation(summary = "Public Channel 생성")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "Public Channel이 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = ChannelResponse.class))
      )
  })
  ResponseEntity<ChannelResponse> create(
      @Parameter(description = "Public Channel 생성 정보")
      @RequestBody(required = true, content = @Content(schema = @Schema(implementation = PublicChannelCreateRequest.class)))
      PublicChannelCreateRequest request
  );

  @Operation(summary = "Private Channel 생성")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "Private Channel이 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = ChannelResponse.class))
      )
  })
  ResponseEntity<ChannelResponse> create(
      @Parameter(description = "Private Channel 생성 정보")
      @RequestBody(required = true, content = @Content(schema = @Schema(implementation = PrivateChannelCreateRequest.class)))
      PrivateChannelCreateRequest request,
      @Parameter(hidden = true)
      @AuthenticationPrincipal DiscodeitUserDetails userDetails
  );

  @Operation(summary = "Channel 정보 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Channel 정보가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = ChannelResponse.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "Channel을 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Channel with id {channelId} not found"))
      ),
      @ApiResponse(
          responseCode = "400", description = "Private Channel은 수정할 수 없음",
          content = @Content(examples = @ExampleObject(value = "Private channel cannot be updated"))
      )
  })
  ResponseEntity<ChannelResponse> update(
      @Parameter(description = "수정할 Channel ID") @PathVariable UUID channelId,
      @Parameter(description = "수정할 Channel 정보")
      @RequestBody(required = true, content = @Content(schema = @Schema(implementation = PublicChannelUpdateRequest.class)))
      PublicChannelUpdateRequest request
  );

  @Operation(summary = "Channel 삭제")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "Channel이 성공적으로 삭제됨"
      ),
      @ApiResponse(
          responseCode = "404", description = "Channel을 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Channel with id {channelId} not found"))
      )
  })
  ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 Channel ID") @PathVariable UUID channelId
  );

  @Operation(summary = "User가 참여 중인 Channel 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Channel 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChannelResponse.class)))
      )
  })
  ResponseEntity<List<ChannelResponse>> findAll(
      @Parameter(description = "조회할 User ID") @RequestParam("userId") UUID userId
  );
}
