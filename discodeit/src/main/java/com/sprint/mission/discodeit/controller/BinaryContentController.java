package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  @RequestMapping(value = "/{binaryContentId}/download", method = RequestMethod.GET)
  public ResponseEntity<byte[]> download(@PathVariable UUID binaryContentId) {
    BinaryContent content = binaryContentService.findEntityById(binaryContentId);

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(content.getContentType()))
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "inline; filename=\"" + content.getFileName() + "\"")
        .body(content.getBytes());
  }

  @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
  public ResponseEntity<BinaryContent> findById(@PathVariable UUID binaryContentId) {
    return ResponseEntity.ok(binaryContentService.findEntityById(binaryContentId));
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<BinaryContent>> findAllByIdIn(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
    List<BinaryContent> contents = binaryContentIds.stream()
        .map(binaryContentService::findEntityById)
        .toList();
    return ResponseEntity.ok(contents);
  }

  @RequestMapping(value = "/find", method = RequestMethod.GET)
  public ResponseEntity<BinaryContent> find(@RequestParam("binaryContentId") UUID binaryContentId) {
    return ResponseEntity.ok(binaryContentService.findEntityById(binaryContentId));
  }

}
