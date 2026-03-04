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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Channel", description = "Channel API")
public interface ChannelApi {

  @Operation(summary = "Pubilc Channel 생성")
  @ApiResponses(value = {
      @ApiResponse (
          responseCode = "201",description = "Channel이 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = Channel.class))
      ),
      @ApiResponse (
          responseCode = "400" , description = "잘못된 요청 데이터",
          content = @Content(examples = @ExampleObject(value = "Invalid channel data"))
      ),
      @ApiResponse(
          responseCode = "404", description = "초대할 멤버를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "User not found"))
      )
  })
  ResponseEntity<Channel> create(
      @Parameter(
          description = "Channel 생성 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      ) PrivateChannelCreateRequest request
  );

  @Operation(summary = "Private Channel 생성")
  @ApiResponses(value = {
      @ApiResponse (
          responseCode = "201",description = "Channel이 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = Channel.class))
      ),
      @ApiResponse (
          responseCode = "400" , description = "잘못된 요청 데이터",
          content = @Content(examples = @ExampleObject(value = "Invalid channel data"))
      ),
      @ApiResponse(
          responseCode = "404", description = "초대할 멤버를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "User not found"))
      )
  })
  ResponseEntity<Channel> create(
      @Parameter(
          description = "Channel 생성 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      )PublicChannelCreateRequest request
  );

  @Operation(summary = "Channel 정보 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Channel 정보가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = Channel.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "Channel을 찾을 수 없음",
          content = @Content(examples = @ExampleObject("Channel with id {channelId} not found"))
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청 데이터 또는 중복된 채널 이름",
          content = @Content(examples = @ExampleObject("Invalid channel data provided"))
      )
  })
  ResponseEntity<Channel> update(
      @Parameter(description = "수정할 채널 ID") UUID channelId,
      @Parameter(description = "수정할 채널 정보") @RequestBody PublicChannelUpdateRequest request
  );

  @Operation(summary = "Channel 삭제")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204",
          description = "Channel이 성공적으로 삭제됨"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Channel을 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Channel with id {id} not found"))
      )
  })
  ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 Channel ID")UUID channelId
  );

  @Operation(summary = "전체 Channel 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",description = "Channel 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChannelDto.class)))
      )
  })
  ResponseEntity<List<ChannelDto>> findAll(
      @Parameter(description = "조회된 UserID") UUID userId
  );
}
