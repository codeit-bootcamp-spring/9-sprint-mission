package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentView;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BinaryContentController implements BinaryContentApi {

  private final BinaryContentService binaryContentService;

  @Override
  public ResponseEntity<byte[]> download(UUID binaryContentId) {
    var content = binaryContentService.findEntityById(binaryContentId);

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(content.getContentType()))
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "inline; filename=\"" + content.getFileName() + "\"")
        .body(content.getBytes());
  }

  @Override
  public BinaryContentView findById(UUID binaryContentId) {
    return binaryContentService.findById(binaryContentId);
  }

  @Override
  public List<BinaryContentView> findAllByIdIn(List<UUID> ids) {
    return binaryContentService.findAllByIdIn(ids);
  }
}
