package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContent")
public class BinaryContentController {

    private final BinaryContentRepository binaryContentRepository;

    @GetMapping("/find")
    public ResponseEntity<BinaryContent> find(
            @RequestParam UUID binaryContentId
    ) {
        return binaryContentRepository.findById(binaryContentId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<BinaryContent>> findAll(
            @RequestParam List<UUID> binaryContentIds
    ) {
        List<BinaryContent> files = binaryContentRepository.findAllByIdIn(binaryContentIds);
        return ResponseEntity.ok(files);
    }
}

