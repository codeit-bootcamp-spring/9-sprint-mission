package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    public BinaryContentController(BinaryContentService binaryContentService) {
        this.binaryContentService = binaryContentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BinaryContentDto upload(
        @RequestParam("file") MultipartFile file
    ) throws IOException {

        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType() != null
            ? file.getContentType()
            : "application/octet-stream";

        return binaryContentService.create(
            fileName,
            file.getBytes(),
            contentType
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> download(@PathVariable UUID id) {

        BinaryContent content = binaryContentService.findEntityById(id);

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(content.getContentType()))
            .contentLength(content.getSize())
            .body(content.getBytes());
    }

    @GetMapping
    public List<BinaryContentDto> getAll(
        @RequestParam List<UUID> ids
    ) {
        return binaryContentService.findAllByIdIn(ids);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        binaryContentService.delete(id);
    }
}