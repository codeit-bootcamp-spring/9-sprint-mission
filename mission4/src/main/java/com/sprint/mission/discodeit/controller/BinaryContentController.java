package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binarycontent")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @RequestMapping(
            path="/find",
            method = RequestMethod.GET
    )
    public ResponseEntity<BinaryContent> find(
            @RequestParam UUID binaryContentId
    ){
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        return ResponseEntity.ok(binaryContent);
    }

    @RequestMapping(
            path="/find/all",
            method = RequestMethod.GET
    )
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(
            @RequestParam List<UUID> binaryContentIds
    ){
        List<BinaryContent> binaryContentList = binaryContentService.findAllByIdIn(binaryContentIds);
        return ResponseEntity.ok(binaryContentList);
    }
}
