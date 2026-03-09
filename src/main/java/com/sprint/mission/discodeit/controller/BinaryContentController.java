package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
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
    private final BinaryContentStorage binaryContentStorage;

    public BinaryContentController(
        BinaryContentService binaryContentService,
        BinaryContentStorage binaryContentStorage
    ) {
        this.binaryContentService = binaryContentService;
        this.binaryContentStorage = binaryContentStorage;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BinaryContentDto upload(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
        byte[] bytes = file.getBytes();

        return binaryContentService.create(fileName, bytes, contentType);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<?> download(@PathVariable UUID id) {
        BinaryContent content = binaryContentService.findEntityById(id);
        BinaryContentDto dto = new BinaryContentDto(
            content.getId(),
            content.getFileName(),
            content.getSize(),
            content.getContentType(),
            content.getCreatedAt()
        );
        return binaryContentStorage.download(dto);
    }

    @GetMapping("/{id}")
    public BinaryContentDto getOne(@PathVariable UUID id) {
        BinaryContent content = binaryContentService.findEntityById(id);
        return new BinaryContentDto(
            content.getId(),
            content.getFileName(),
            content.getSize(),
            content.getContentType(),
            content.getCreatedAt()
        );
    }

    @GetMapping
    public List<BinaryContentDto> getAll(@RequestParam List<UUID> ids) {
        return binaryContentService.findAllByIdIn(ids);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        binaryContentService.delete(id);
    }
}