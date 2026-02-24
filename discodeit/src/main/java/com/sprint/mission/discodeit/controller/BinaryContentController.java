package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/binary-contents")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    public BinaryContentController(BinaryContentService binaryContentService) {
        this.binaryContentService = binaryContentService;
    }

    @PostMapping
    public BinaryContent create(@RequestBody BinaryContentCreateRequest request) {
        return binaryContentService.create(request);
    }

    @GetMapping("/{binaryContentId}")
    public BinaryContent find(@PathVariable UUID binaryContentId) {
        return binaryContentService.find(binaryContentId);
    }
}

