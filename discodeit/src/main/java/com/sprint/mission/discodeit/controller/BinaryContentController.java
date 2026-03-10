package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.jpa.BinaryContentJpaRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class BinaryContentController implements BinaryContentApi {

    private final BinaryContentService service;
    private final BinaryContentStorage storage;

  @Override
  public ResponseEntity<BinaryContent> getBinaryContent(UUID binaryContentId) {
    BinaryContent binaryContent = service.find(binaryContentId);
    return ResponseEntity.ok(binaryContent);
  }

  @Override
  public ResponseEntity<List<BinaryContent>> getBinaryContents(List<UUID> binaryContentIds) {
    List<BinaryContent> contents = service.findAllByIdIn(binaryContentIds);
    return ResponseEntity.ok(contents);
  }

    @GetMapping("/api/binaryContents/{binaryContentId}/download")
    public ResponseEntity<?> download(@PathVariable UUID binaryContentId) {
      BinaryContentDto dto = service.get(binaryContentId);
      return storage.download(dto);
    }
  }
