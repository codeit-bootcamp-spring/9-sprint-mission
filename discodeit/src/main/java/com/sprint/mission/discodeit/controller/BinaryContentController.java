package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController implements BinaryContentApi {

  private final BinaryContentService binaryContentService;

  @Override
  public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
      @RequestParam List<UUID> binaryContentIds) {
    return ResponseEntity.ok(binaryContentService.findAllByIdIn(binaryContentIds));
  }

  @Override
  public ResponseEntity<BinaryContentDto> find(@PathVariable UUID binaryContentId) {
    return ResponseEntity.ok(binaryContentService.findById(binaryContentId));
  }

  @Override
  public ResponseEntity<Resource> download(
      @PathVariable(name = "binaryContentId") UUID binaryContentId) {
    return binaryContentService.download(binaryContentId);
  }
}