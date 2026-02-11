package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContent")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @PostMapping("/create")
    public ResponseEntity<BinaryContent> create(
            @RequestBody BinaryContentCreateRequest request
    ) {
        BinaryContent created = binaryContentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/find")
    public ResponseEntity<BinaryContent> find(
            @RequestParam UUID binaryContentId
    )   {
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        return ResponseEntity.ok(binaryContent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        binaryContentService.delete(id);
        return ResponseEntity.noContent().build();
    }


}

