package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "첨부 파일 API")
@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {

	private final BinaryContentService binaryContentService;

	@Operation(summary = "첨부 파일 조회")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공",
					content = @Content(schema = @Schema(implementation = BinaryContent.class))),
			@ApiResponse(responseCode = "404", description = "첨부 파일을 찾을 수 없음",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@GetMapping("/{binaryContentId}")
	public ResponseEntity<BinaryContent> find(
			@PathVariable UUID binaryContentId
	) {
		BinaryContent binaryContent = binaryContentService.find(binaryContentId);
		return ResponseEntity.ok(binaryContent);
	}

	@Operation(summary = "여러 첨부 파일 조회")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "첨부 파일 목록 조회 성공",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = BinaryContent.class))))
	})
	@GetMapping
	public ResponseEntity<List<BinaryContent>> findAllByIdIn(
			@RequestParam List<UUID> binaryContentIds
	) {
		List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
		return ResponseEntity.ok(binaryContents);
	}
}
