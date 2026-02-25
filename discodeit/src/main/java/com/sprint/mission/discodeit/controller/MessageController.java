package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "Message API")
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

	private final MessageService messageService;

	@Operation(summary = "Message 생성")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨",
					content = @Content(schema = @Schema(implementation = Message.class))),
			@ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
					mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
					encoding = @Encoding(name = "messageCreateRequest", contentType = MediaType.APPLICATION_JSON_VALUE)
			)
	)
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Message> create(
			@RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
			@RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
	) throws IOException {
		List<BinaryContentCreateRequest> attachmentRequests = resolveAttachments(attachments);
		Message message = messageService.create(messageCreateRequest, attachmentRequests);
		return ResponseEntity.status(HttpStatus.CREATED).body(message);
	}

	@Operation(summary = "Channel의 Message 목록 조회")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Message 목록 조회 성공",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = Message.class))))
	})
	@GetMapping
	public ResponseEntity<List<Message>> findAllByChannelId(
			@RequestParam UUID channelId
	) {
		List<Message> messages = messageService.findAllByChannelId(channelId);
		return ResponseEntity.ok(messages);
	}

	@Operation(summary = "Message 내용 수정")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨",
					content = @Content(schema = @Schema(implementation = Message.class))),
			@ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PatchMapping("/{messageId}")
	public ResponseEntity<Message> update(
			@PathVariable UUID messageId,
			@RequestBody MessageUpdateRequest request
	) {
		Message message = messageService.update(messageId, request);
		return ResponseEntity.ok(message);
	}

	@Operation(summary = "Message 삭제")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨", content = @Content),
			@ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@DeleteMapping("/{messageId}")
	public ResponseEntity<Void> delete(
			@PathVariable UUID messageId
	) {
		messageService.delete(messageId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * MultipartFile 목록을 BinaryContentCreateRequest 목록으로 변환합니다.
	 * 비어있는 파일은 제외하며, 첨부파일이 없으면 빈 리스트를 반환합니다.
	 */
	private List<BinaryContentCreateRequest> resolveAttachments(List<MultipartFile> attachments) throws IOException {
		if (attachments == null || attachments.isEmpty()) {
			return new ArrayList<>();
		}
		List<BinaryContentCreateRequest> result = new ArrayList<>();
		for (MultipartFile file : attachments) {
			if (!file.isEmpty()) {
				result.add(new BinaryContentCreateRequest(
						file.getOriginalFilename(),
						file.getContentType(),
						file.getBytes()
				));
			}
		}
		return result;
	}
}
