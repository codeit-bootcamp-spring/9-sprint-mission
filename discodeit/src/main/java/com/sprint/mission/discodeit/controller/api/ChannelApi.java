package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "Channel API")
public interface ChannelApi {

    @Operation(summary = "공개 Channel 생성")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "공개 Channel이 성공적으로 생성됨",
                    content = @Content(schema = @Schema(implementation = Channel.class))
            )
    })
    ResponseEntity<Channel> create(
            @Parameter(
                    description = "공개 Channel 생성 정보",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ) PublicChannelCreateRequest request
    );

    @Operation(summary = "비공개 Channel 생성")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "비공개 Channel이 성공적으로 생성됨",
                    content = @Content(schema = @Schema(implementation = Channel.class))
            )
    })
    ResponseEntity<Channel> create(
            @Parameter(
                    description = "비공개 Channel 생성 정보",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ) PrivateChannelCreateRequest request
    );

    @Operation(summary = "Channel 수정")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Channel이 성공적으로 수정됨",
                    content = @Content(schema = @Schema(implementation = Channel.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "Channel을 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Channel with id {channelId} not found"))
            )
    })
    ResponseEntity<Channel> update(
            @Parameter(description = "수정할 Channel ID") @PathVariable("channelId") UUID channelId,
            @Parameter(
                    description = "수정할 Channel 정보",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ) PublicChannelUpdateRequest request
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
            @Parameter(description = "삭제할 Channel ID") @PathVariable("channelId") UUID channelId
    );

    @Operation(summary = "특정 User가 참여 중인 Channel 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Channel 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            )
    })
    ResponseEntity<List<ChannelDto>> findAllByUserId(
            @Parameter(description = "User ID") @RequestParam("userId") UUID userId,
            @Parameter(description = "페이지 번호") @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(value = "size", defaultValue = "10") int size
    );
}