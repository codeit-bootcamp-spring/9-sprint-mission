package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.sprint.mission.discodeit.entity.Channel;
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
}
