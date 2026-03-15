package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // 파일 업로드
    public BinaryContentDto upload(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
        byte[] bytes = file.getBytes();

        // 서비스에서 DTO로 변환하여 반환
        return binaryContentService.create(fileName, bytes, contentType);
    }

    @GetMapping("/{id}/download")
    // 파일 다운로드
    public ResponseEntity<?> download(@PathVariable UUID id) {
        BinaryContentDto dto = binaryContentService.findById(id); // 서비스에서 DTO 반환
        return binaryContentStorage.download(dto); // 다운로드 처리
    }

    @GetMapping("/{id}")
    // 단일 파일 조회
    public BinaryContentDto getOne(@PathVariable UUID id) {
        return binaryContentService.findById(id); // 서비스에서 DTO 반환
    }

    @GetMapping
    // 여러 파일 조회
    public List<BinaryContentDto> getAll(@RequestParam List<UUID> ids) {
        return binaryContentService.findAllByIdIn(ids); // 서비스에서 DTO 반환
    }

    @DeleteMapping("/{id}")
    // 파일 삭제
    public void delete(@PathVariable UUID id) {
        binaryContentService.delete(id);
    }
}