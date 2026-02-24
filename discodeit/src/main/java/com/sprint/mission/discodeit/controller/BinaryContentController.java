package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binary-contents")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> download(@PathVariable UUID id) {
        BinaryContent content = binaryContentService.find(id);

        String contentDisposition = "attachment; filename=\"file_" + id + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition) // 다운로드 파일명 설정
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // 이진 데이터 타입 설정
                .body(content.getBytes()); // 실제 바이트 데이터
    }

    @PostMapping
    public ResponseEntity<BinaryContent> create(@RequestBody BinaryContentCreateRequest request) {
        BinaryContent savedContent = binaryContentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedContent);
    }

    @GetMapping
    public ResponseEntity<List<BinaryContent>> findAll(@RequestParam List<UUID> ids) {
        List<BinaryContent> contents = binaryContentService.findAllByIds(ids);
        return ResponseEntity.ok(contents);
    }
}