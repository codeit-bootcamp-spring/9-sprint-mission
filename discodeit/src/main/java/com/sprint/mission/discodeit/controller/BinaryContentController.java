package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binarycontents")
public class BinaryContentController implements BinaryContentApi {

  private final BinaryContentService binaryContentService;

  @PostMapping(
      path = "/findAll"
      //consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
  )
  @Override
  public ResponseEntity<List<BinaryContent>> findAllByIds(
      @RequestBody List<UUID> ids
  ) {
    List<BinaryContent> contents = binaryContentService.findAllByIdIn(ids);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(contents);
  }


  @GetMapping("/{id}")
  @Override
  public ResponseEntity<BinaryContent> find(
      @PathVariable UUID id) {
    BinaryContent content = binaryContentService.find(id);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(content);
  }
}
