package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController implements BinaryContentApi {

  private final BinaryContentService binaryContentService;

  @Override
  public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
      @RequestParam List<UUID> binaryContentIds) {
    log.debug("Fetching multiple binary contents, count: {}", binaryContentIds.size());
    return ResponseEntity.ok(binaryContentService.findAllByIdIn(binaryContentIds));
  }

  @Override
  public ResponseEntity<BinaryContentDto> find(@PathVariable UUID binaryContentId) {
    log.debug("Fetching metadata for binary content: {}", binaryContentId);
    return ResponseEntity.ok(binaryContentService.findById(binaryContentId));
  }

  @Override
  public ResponseEntity<Resource> download(@PathVariable UUID binaryContentId) {
    log.info("File download request for binary content id: {}", binaryContentId);

    BinaryContentDto dto = binaryContentService.findById(binaryContentId);
    Resource resource = binaryContentService.download(binaryContentId);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dto.fileName() + "\"")
        .contentType(MediaType.parseMediaType(dto.contentType()))
        .body(resource);
  }
}