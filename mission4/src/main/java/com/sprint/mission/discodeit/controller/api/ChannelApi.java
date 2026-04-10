package com.sprint.mission.discodeit.controller.api;


import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name = "Channel", description = "Channel API")
public interface ChannelApi {

  @Operation(summary = "Public Channel 생성", operationId = "create_3")
  @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨",
      content = @Content(schema = @Schema(implementation = ChannelDto.class)))
  public ResponseEntity<ChannelDto> createPublic(

      @RequestBody PublicChannelCreateRequest publicChannelCreateRequest);

  @Operation(summary = "Private Channel 생성")
  @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨",
      content = @Content(schema = @Schema(implementation = ChannelDto.class)))
  public ResponseEntity<ChannelDto> createPrivate(
      @RequestBody PrivateChannelCreateRequest privateChannelCreateRequest);


  @Operation(summary = "User가 참여 중인 Channel 목록 조회", operationId = "findAll_1")
  @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChannelDto.class))))
  public ResponseEntity<List<ChannelDto>> findAll(
      @Parameter(description = "조회할 User ID") @RequestParam UUID userId);

  @Operation(summary = "Channel 정보 수정", operationId = "update_3")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = ChannelDto.class))),
      @ApiResponse(responseCode = "400", description = "Private 채널은 수정할 수 없음",
          content = @Content(examples = @ExampleObject("Private channel cannot be updated"))),
      @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
          content = @Content(examples = @ExampleObject("Channel with id {channelId} not found")))
  })
  public ResponseEntity<ChannelDto> update(
      @Parameter(description = "수정할 ChannelId", required = true) @PathVariable UUID channelId,
      @RequestBody PublicChannelUpdateRequest publicChannelUpdateRequest);

  @Operation(summary = "Channel 삭제", operationId = "delete_2")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "성공적으로 삭제됨."),
      @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
          content = @Content(examples = @ExampleObject("Channel with id {channelId} not found")))
  })
  public ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 Channel ID", required = true) @PathVariable UUID channelId);


}
