package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/binary-contents")
public class BinaryContentController implements BinaryContentApi {

    private final BinaryContentService binaryContentService;

    public BinaryContentController(BinaryContentService binaryContentService) {
        this.binaryContentService = binaryContentService;
    }

    @PostMapping
    @Override
    public BinaryContent create(@RequestBody BinaryContentCreateRequest request) {
        return binaryContentService.create(request);
    }

    @GetMapping("/{binaryContentId}")
    @Override
    public BinaryContent find(@PathVariable UUID binaryContentId) {
        return binaryContentService.find(binaryContentId);
    }
}

