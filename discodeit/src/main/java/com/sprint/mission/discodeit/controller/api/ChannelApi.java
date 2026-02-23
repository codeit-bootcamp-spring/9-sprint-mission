package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
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
import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;


@Tag(name = "Channel", description = "Channel API")
public interface ChannelApi {

  @Operation(summary = "Public Channel 등록")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "Public Channel이 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = Channel.class))
      )
  })
  ResponseEntity<Channel> createPublic(
      @Parameter(
          description = "Public Channel 생성 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      ) PublicChannelCreateRequest publicChannelCreateRequest,
      @Parameter(
          description = "Public Channel 프로필 이미지",
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
      ) MultipartFile profile
  );

  @Operation(summary = "Private Channel 등록")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "Private Channel이 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = Channel.class))
      )
  })
  ResponseEntity<Channel> createPrivate(
      @Parameter(
          description = "Private Channel 생성 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      ) PrivateChannelCreateRequest privateChannelCreateRequest,
      @Parameter(
          description = "Private Channel 프로필 이미지",
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
      ) MultipartFile profile
  );

  @Operation(summary = "Channel 정보 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Channel 정보가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = Channel.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "Channel 찾을 수 없음",
          content = @Content(examples = @ExampleObject("Channel with id {channelId} not found"))
      ),
      @ApiResponse(
          responseCode = "400", description = "같은 channel이 이미 존재함",
          content = @Content(examples = @ExampleObject("channel {channelId} already exists"))
      )
  })
  ResponseEntity<Channel> update(
      @Parameter(description = "수정할 Channel ID") UUID channelId,
      @Parameter(description = "수정할 Channel 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) PublicChannelUpdateRequest publicChannelUpdateRequest,
      @Parameter(description = "수정할 profile 정보") MultipartFile profile
  );

  @Operation(summary = "Channel 삭제")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204",
          description = "Channel을 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Channel with id {id} not found"))
      )
  })
  ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 Channel ID") UUID channelId
  );

  @Operation(summary = "User 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "User 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChannelDto.class)))
      )
  })
  ResponseEntity<List<ChannelDto>> findAllByUserId(
      @Parameter(description = "조회할 UserID") UUID userId);
}
