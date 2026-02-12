package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContent")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @PostMapping("/create")
    public ResponseEntity<BinaryContentResponse> create(@RequestBody BinaryContentCreateRequest request) {
        BinaryContent content = binaryContentService.create(request);

        if (content == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(new BinaryContentResponse(
                content.getId(),
                content.getContentType(),
                content.getFileName(),
                content.getFileSize(),
                content.getBytes()
        ));
    }

    @GetMapping("/find")
    public ResponseEntity<BinaryContentResponse> find(@RequestParam UUID binaryContentId) {
        return binaryContentService.findById(binaryContentId)
                .map(content -> ResponseEntity.ok(new BinaryContentResponse(
                        content.getId(),
                        content.getContentType(),
                        content.getFileName(),
                        content.getFileSize(),
                        content.getBytes()
                )))
                .orElse(ResponseEntity.notFound().build());
    }
}