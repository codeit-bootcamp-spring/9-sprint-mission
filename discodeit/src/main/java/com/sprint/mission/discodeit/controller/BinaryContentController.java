package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/binary")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    public BinaryContentController(BinaryContentService binaryContentService) {
        this.binaryContentService = binaryContentService;
    }

    // [ ] 바이너리 파일을 1개 조회(다운로드)
    @RequestMapping(value = "/binary-contents/{binaryContentId}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadFile(@PathVariable UUID binaryContentId) {

        // 파일 정보 조회
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);

        // byte[]를 Resource로 변환
        ByteArrayResource resource = new ByteArrayResource(binaryContent.getBytes());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(binaryContent.getContentType()))
                .contentLength(binaryContent.getSize())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + binaryContent.getFileName() + "\"")
                .body(resource);
    }

    // [ ] 바이너리 파일을 여러 개 조회 (메타데이터)
    @RequestMapping(value = "/binary-contents", method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContent>> getMultipleFiles(
            @RequestParam("ids") List<UUID> ids) {

        List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(ids);
        return ResponseEntity.ok(binaryContents);
    }
}
