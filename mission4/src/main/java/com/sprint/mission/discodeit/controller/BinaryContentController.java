package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.local.BinaryContentStorage;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
public class BinaryContentController implements BinaryContentApi {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage storage;

  @Override
  @GetMapping("/{binaryContentId}")
  public ResponseEntity<BinaryContentDto> find(
      @PathVariable UUID binaryContentId
  ) {
    BinaryContentDto binaryContent = binaryContentService.find(binaryContentId);
    return ResponseEntity.ok(binaryContent);
  }

  @Override
  @GetMapping
  public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
      @RequestParam List<UUID> binaryContentIds
  ) {
    List<BinaryContentDto> binaryContentList = binaryContentService.findAllByIdIn(binaryContentIds);
    return ResponseEntity.ok(binaryContentList);
  }

  @Override
  @GetMapping("/{binaryContentId}/download")
  public ResponseEntity<Resource> download(
      @PathVariable UUID binaryContentId
  ) {
    BinaryContentDto binaryContent = binaryContentService.find(binaryContentId);
    InputStream inputStream = storage.get(binaryContentId);
    Resource resource = new InputStreamResource(inputStream);

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(binaryContent.contentType()))
        .contentLength(binaryContent.size())
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
        .body(resource);
  }
}


