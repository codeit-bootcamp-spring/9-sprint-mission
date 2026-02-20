package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
@CrossOrigin(origins = "*") // 테스트 때문에 허용
public class BinaryContentController implements BinaryContentApi {
    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> find(@PathVariable UUID binaryContentId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContentService.find(binaryContentId));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<List<BinaryContent>> findByIds(@RequestBody List<UUID> idList){
        List<BinaryContent> binaryContents = binaryContentService.findAllByIn(idList);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContents);
    }
}
