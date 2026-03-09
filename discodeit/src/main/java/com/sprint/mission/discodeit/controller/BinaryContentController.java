package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.jpa.BinaryContentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class BinaryContentController implements BinaryContentApi {

  private final BinaryContentJpaRepository binaryContentRepository;

  @Override
  public ResponseEntity<BinaryContent> getBinaryContent(UUID binaryContentId) {

    return binaryContentRepository.findById(binaryContentId)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Override
  public ResponseEntity<List<BinaryContent>> getBinaryContents(List<UUID> binaryContentIds) {

    List<BinaryContent> files =
        binaryContentRepository.findAllById(binaryContentIds);

    return ResponseEntity.ok(files);
  }
}