package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "Channel API")
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

	private final ChannelService channelService;

	@Operation(summary = "Public Channel 생성")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨",
					content = @Content(schema = @Schema(implementation = Channel.class)))
	})
	@PostMapping("/public")
	public ResponseEntity<Channel> createPublicChannel(@RequestBody PublicChannelCreateRequest request) {
		Channel channel = channelService.createPublic(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(channel);
	}

	@Operation(summary = "Private Channel 생성")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨",
					content = @Content(schema = @Schema(implementation = Channel.class)))
	})
	@PostMapping("/private")
	public ResponseEntity<Channel> createPrivateChannel(@RequestBody PrivateChannelCreateRequest request) {
		Channel channel = channelService.createPrivate(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(channel);
	}

	@Operation(summary = "User가 참여 중인 Channel 목록 조회")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChannelDto.class))))
	})
	@GetMapping
	public ResponseEntity<List<ChannelDto>> findAllByUserId(
			@RequestParam UUID userId
	) {
		List<ChannelDto> channels = channelService.findAllByUserId(userId);
		return ResponseEntity.ok(channels);
	}

	@Operation(summary = "Channel 정보 수정")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨",
					content = @Content(schema = @Schema(implementation = Channel.class))),
			@ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "400", description = "Private Channel은 수정할 수 없음",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PatchMapping("/{channelId}")
	public ResponseEntity<Channel> update(
			@PathVariable UUID channelId,
			@RequestBody PublicChannelUpdateRequest request
	) {
		Channel channel = channelService.update(channelId, request);
		return ResponseEntity.ok(channel);
	}

	@Operation(summary = "Channel 삭제")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨", content = @Content),
			@ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@DeleteMapping("/{channelId}")
	public ResponseEntity<Void> delete(
			@PathVariable UUID channelId
	) {
		channelService.delete(channelId);
		return ResponseEntity.noContent().build();
	}
}
