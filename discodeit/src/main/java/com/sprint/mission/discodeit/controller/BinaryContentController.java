package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BinaryContentController implements BinaryContentApi {

  private final BinaryContentService binaryContentService;

  @Override
  public ResponseEntity<List<BinaryContentResponse>> findAllByIdIn(List<UUID> binaryContentIds) {
    List<BinaryContentResponse> responses = binaryContentService.findAllByIdIn(binaryContentIds)
        .stream()
        .map(this::convertToResponse)
        .toList();
    return ResponseEntity.ok(responses);
  }

  @Override
  public ResponseEntity<BinaryContentResponse> find(
      @PathVariable UUID binaryContentId) {
    return binaryContentService.findById(binaryContentId)
        .map(this::convertToResponse)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  private BinaryContentResponse convertToResponse(BinaryContent content) {
    return new BinaryContentResponse(
        content.getId(),
        content.getCreatedAt(),
        content.getFileName(),
        content.getSize(),
        content.getContentType(),
        content.getBytes()
    );
  }
}