package com.sprint.mission.discordit.controller;

import com.sprint.mission.discordit.entity.BinaryContent;
import com.sprint.mission.discordit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name = "BinaryContentController", description = "바이너리 컨텐츠 조회")
@RequiredArgsConstructor
@Controller
@ResponseBody
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  @Operation(summary = "단건 조회", description = "binaryContentId를 통한 단건 조회")
  @ApiResponse(responseCode = "200", description = "binaryContent 단건 조회 성공")
  @RequestMapping(path = "{binaryContentId}", method = RequestMethod.GET)
  public ResponseEntity<BinaryContent> find(@PathVariable UUID binaryContentId) {
    BinaryContent binaryContent = binaryContentService.find(binaryContentId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContent);
  }

  @Operation(summary = "다건 조회", description = "binaryContentId 여러개를 한번에 조회 가능")
  @ApiResponse(responseCode = "200", description = "binaryContent 다건 조회 성공")
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<BinaryContent>> findAllByIdIn(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
    List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContents);
  }
}
