package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContents")
public class BinaryContentController implements BinaryContentApi {

  private final BinaryContentService binaryContentService;
  // 다운로드 기능을 위해 storage를 사용
  private final BinaryContentStorage binaryContentStorage;

  @GetMapping("/{binaryContentId}")
  @Override
  public ResponseEntity<BinaryContentDto> find(
      @PathVariable("binaryContentId") UUID binaryContentId) {
    BinaryContentDto binaryContentDto = binaryContentService.find(binaryContentId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContentDto);
  }

  @GetMapping
  @Override
  public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
    List<BinaryContentDto> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContents);
  }

  @GetMapping("/{binaryContentId}/download")
  public ResponseEntity<?> download(@PathVariable("binaryContentId") UUID binaryContentId) {
    //서비스에서 DTO(파일 정보)가져오기
    BinaryContentDto metaData = binaryContentService.find(binaryContentId);

    //storage의 download기능으로 진짜 파일 쏴주기
    return binaryContentStorage.download(metaData);
  }


}
