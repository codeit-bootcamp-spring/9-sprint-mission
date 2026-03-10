package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;

  @GetMapping("/{binaryContentId}")
  public ResponseEntity<?> downloadDirect(@PathVariable("binaryContentId") UUID binaryContentId) {
    BinaryContentDto metaData = binaryContentService.find(binaryContentId);
    return binaryContentStorage.download(metaData);
  }

  // Case 2: 프론트가 /api/binaryContents/{UUID}/download 로 요청할 때 (아까 로그 상황)
  @GetMapping("/{binaryContentId}/download")
  public ResponseEntity<?> downloadPath(@PathVariable("binaryContentId") UUID binaryContentId) {
    BinaryContentDto metaData = binaryContentService.find(binaryContentId);
    return binaryContentStorage.download(metaData);
  }

  @GetMapping("/{binaryContentId}/meta")
  public ResponseEntity<BinaryContentDto> find(
      @PathVariable("binaryContentId") UUID binaryContentId) {
    BinaryContentDto binaryContentDto = binaryContentService.find(binaryContentId);
    return ResponseEntity.ok(binaryContentDto);
  }

}