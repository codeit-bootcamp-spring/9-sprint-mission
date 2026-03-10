package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Message;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "Message API")
public interface MessageApi {

    @Operation(summary = "Message 생성")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "Message가 성공적으로 생성됨",
                    content = @Content(schema = @Schema(implementation = Message.class))
            )
    })
    ResponseEntity<Message> create(
            @Parameter(
                    description = "Message 생성 정보",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ) @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
            @Parameter(
                    description = "첨부 파일 목록",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            ) @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    );

    @Operation(summary = "Message 수정")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Message가 성공적으로 수정됨",
                    content = @Content(schema = @Schema(implementation = Message.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "Message를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found"))
            )
    })
    ResponseEntity<Message> update(
            @Parameter(description = "수정할 Message ID") @PathVariable("messageId") UUID messageId,
            @Parameter(
                    description = "수정할 Message 정보",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ) MessageUpdateRequest request
    );

    @Operation(summary = "Message 삭제")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204", description = "Message가 성공적으로 삭제됨"
            ),
            @ApiResponse(
                    responseCode = "404", description = "Message를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found"))
            )
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 Message ID") @PathVariable("messageId") UUID messageId
    );

    @Operation(summary = "특정 채널의 메시지 목록 조회 (페이지네이션)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "메시지 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            )
    })
    ResponseEntity<PageResponse<Message>> findAllByChannelId(
            @Parameter(description = "조회할 채널 ID") @RequestParam("channelId") UUID channelId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "한 페이지당 데이터 개수") @RequestParam(value = "size", defaultValue = "10") int size
    );
}