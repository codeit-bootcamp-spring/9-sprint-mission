package com.sprint.mission.discordit.controller;

import com.sprint.mission.discordit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discordit.dto.request.MessageCreateRequest;
import com.sprint.mission.discordit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discordit.entity.Message;
import com.sprint.mission.discordit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "message API", description = "메시지 생성 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController {

  private final MessageService messageService;

  @Operation(summary = "메시지 생성")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "메시지 생성 성공"),
          @ApiResponse(responseCode = "404", description = "채널 혹은 사용자가 존재하지 않을 경우", content = @Content)
      }
  )
  @RequestMapping(
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      method = RequestMethod.POST
  )
  public ResponseEntity<Message> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    List<BinaryContentCreateRequest> attachmentRequests = Optional.ofNullable(attachments)
        .map(files -> files.stream()
            .map(file -> {
              try {
                return new BinaryContentCreateRequest(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
                );
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
            .toList())
        .orElse(new ArrayList<>());
    Message createdMessage = messageService.create(messageCreateRequest, attachmentRequests);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdMessage);
  }

  @Operation(summary = "메시지 수정")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "메시지 수정 성공"),
      @ApiResponse(responseCode = "404", description = "해당 메시지가 존재하지 않을 경우", content = @Content)
  })
  @RequestMapping(path = "{messageId}")
  public ResponseEntity<Message> update(@PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest request) {
    Message updatedMessage = messageService.update(messageId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedMessage);
  }

  @Operation(summary = "메시지 삭제")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "메시지 삭제 성공"),
      @ApiResponse(responseCode = "404", description = "해당 메시지가 존재하지 않을 경우", content = @Content)
  })
  @RequestMapping(path = "{messageId}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @Operation(summary = "채널 내 메시지 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "메시지 조회 성공")
  })
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<Message>> findAllByChannelId(
      @RequestParam("channelId") UUID channelId) {
    List<Message> messages = messageService.findAllByChannelId(channelId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(messages);
  }
}
