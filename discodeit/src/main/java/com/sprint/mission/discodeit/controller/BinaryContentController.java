package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Tag(name = "BinaryContentController", description = "바이너리 컨텐츠 조회")
@RequiredArgsConstructor
@Controller
@ResponseBody
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;

  @Operation(summary = "단건 조회", description = "binaryContentId를 통한 단건 조회")
  @ApiResponse(responseCode = "200", description = "binaryContent 단건 조회 성공")
  @GetMapping(path = "{binaryContentId}")
  public ResponseEntity<BinaryContentDto> find(@PathVariable UUID binaryContentId) {
    log.debug("binaryContent 단건 조회 진입: {}", binaryContentId);
    BinaryContentDto binaryContent = binaryContentService.find(binaryContentId);
    log.info("binaryContent 단건 조회 성공: {}", binaryContent);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContent);
  }

  @Operation(summary = "다건 조회", description = "binaryContentId 여러개를 한번에 조회 가능")
  @ApiResponse(responseCode = "200", description = "binaryContent 다건 조회 성공")
  @GetMapping
  public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
    log.debug("binaryContent 다건 조회 진입: {}", binaryContentIds);
    List<BinaryContentDto> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
    log.info("binaryContent 다건 조회 완료: {}", binaryContents);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContents);
  }

  @Operation(summary = "다운로드", description = "bianryContentId를 통한 다운로드")
  @ApiResponse(responseCode = "200", description = "다운로드 성공")
  @GetMapping("{binaryContentId}/download")
  public ResponseEntity<?> download(@PathVariable UUID binaryContentId) {
    log.debug("binaryContent 다운로드 진입: {}", binaryContentId);
    BinaryContentDto dto = binaryContentService.find(binaryContentId);
    log.info("binaryContent 다운로드 완료: {}", binaryContentId);
    return binaryContentStorage.download(dto);
  }
}
