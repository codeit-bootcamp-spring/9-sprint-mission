package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  @GetMapping("/{binaryContentId}/image")
  public ResponseEntity<byte[]> getImage(@PathVariable UUID binaryContentId) {
    BinaryContent content = binaryContentService.find(binaryContentId);

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(content.getContentType()))
        .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
        .body(content.getBytes()); // 실제 이미지 바이트
  }

  @GetMapping("/{binaryContentId}")
  public ResponseEntity<BinaryContent> find(@PathVariable("binaryContentId") UUID binaryContentId) {
    BinaryContent binaryContent = binaryContentService.find(binaryContentId);
    return ResponseEntity.ok(binaryContent);
  }
}