package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@ControllerAdvice
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
@CrossOrigin(origins = "*") // 테스트 때문에 허용
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> find(@PathVariable UUID binaryContentId){
        return ResponseEntity.ok(binaryContentService.find(binaryContentId));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<List<BinaryContent>> findByIds(@RequestBody List<UUID> idList){
        return ResponseEntity.ok(binaryContentService.findAllByIn(idList));
    }
}
