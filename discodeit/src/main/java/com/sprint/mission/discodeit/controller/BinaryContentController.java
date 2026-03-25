package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
public class BinaryContentController implements BinaryContentApi {
    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @GetMapping("/{binaryContentId}")
    public ResponseEntity<BinaryContentDto> find(@PathVariable UUID binaryContentId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContentService.find(binaryContentId));
    }

    @PostMapping()
    public ResponseEntity<List<BinaryContentDto>> findByIds(@RequestBody List<UUID> idList){
        List<BinaryContentDto> binaryContents = binaryContentService.findAllByIn(idList);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContents);
    }

    @GetMapping("/{binaryContentId}/download")
    public ResponseEntity<?> download(@PathVariable UUID binaryContentId){
        log.info("파일 다운로드 요청 수신: binaryContentId={}", binaryContentId);
        BinaryContentDto binaryContentDto = binaryContentService.find(binaryContentId);

        ResponseEntity<?> responseEntity = binaryContentStorage.download(binaryContentDto);
        log.debug("파일 다운로드 완료: binaryContentId={}", binaryContentDto.id());
        return responseEntity;
    }
}
