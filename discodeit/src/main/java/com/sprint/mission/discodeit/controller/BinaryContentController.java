package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binarycontents")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @PostMapping(
            path = "/findAll",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<List<BinaryContent>> findAllByIds(
            @RequestPart("ids") List<UUID> ids // 파트로 UUID 리스트를 받음
    ) {
        List<BinaryContent> contents = binaryContentService.findAllByIdIn(ids);
        return ResponseEntity.ok(contents);
    }


    @GetMapping("/{id}")
    public ResponseEntity<BinaryContent> find(
            @PathVariable UUID id) {
        BinaryContent content = binaryContentService.find(id);
        return ResponseEntity.ok(content);
    }
}
