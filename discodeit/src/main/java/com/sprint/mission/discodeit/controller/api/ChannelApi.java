package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "채널 관리 API")
public interface ChannelApi {

  @Operation(summary = "공개 채널 생성")
  @ApiResponse(responseCode = "201", description = "공개 채널이 생성됨")
  ResponseEntity<ChannelDto> create(PublicChannelCreateRequest request);

  @Operation(summary = "비공개 채널 생성")
  @ApiResponse(responseCode = "201", description = "비공개 채널이 생성됨")
  ResponseEntity<ChannelDto> create(PrivateChannelCreateRequest request);

  @Operation(summary = "채널 정보 수정")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "수정 성공"),
      @ApiResponse(responseCode = "404", description = "채널을 찾을 수 없음")
  })
  ResponseEntity<ChannelDto> update(
      @Parameter(description = "수정할 채널 ID") UUID channelId,
      PublicChannelUpdateRequest request
  );

  @Operation(summary = "채널 삭제")
  @ApiResponse(responseCode = "204", description = "삭제 성공")
  ResponseEntity<Void> delete(@Parameter(description = "삭제할 채널 ID") UUID channelId);

  @Operation(summary = "사용자별 채널 목록 조회")
  @ApiResponse(responseCode = "200", description = "조회 성공")
  ResponseEntity<PageResponse<ChannelDto>> findAll(
      @Parameter(description = "사용자 ID") UUID userId,
      @Parameter(hidden = true) Pageable pageable);
}