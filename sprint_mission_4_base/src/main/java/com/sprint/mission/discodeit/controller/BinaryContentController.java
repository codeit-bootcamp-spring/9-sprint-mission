package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attachments")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @PostMapping
    public ResponseEntity<BinaryContent> upload(@RequestParam("file") MultipartFile file) throws IOException {

        // 1. Postman에서 보낸 파일을 우리 시스템(DTO)에 맞게 변환
        BinaryContentCreateRequest request = new BinaryContentCreateRequest(
                file.getOriginalFilename(), // 파일 원본 이름 (예: cat.jpg)
                file.getContentType(),      // 파일 타입 (예: image/jpeg)
                file.getBytes()             // 파일 알맹이 (데이터)
        );

        // 2. 서비스에게 저장하라고 시킴
        BinaryContent content = binaryContentService.create(request);

        return ResponseEntity.ok(content);
    }

    // [1] 파일 1개 다운로드 (핵심 기능)
    @GetMapping("/{contentId}")
    public ResponseEntity<byte[]> downloadContent(@PathVariable UUID contentId) {

        // 1. 서비스에서 파일 정보(엔티티)를 가져옵니다.
        BinaryContent content = binaryContentService.find(contentId);

        // 2. 한글 파일명이 깨지지 않도록 인코딩합니다.
        String encodedFileName = URLEncoder.encode(content.getFileName(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        // 3. 헤더 설정 & 응답 반환
        return ResponseEntity.ok()
                // 브라우저에게 다운로드 창을 띄우라고 알림
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                // 파일의 종류(이미지? 텍스트? PDF?)를 알림
                .contentType(MediaType.parseMediaType(content.getContentType()))
                // 파일의 실제 크기를 알림
                .contentLength(content.getBytes().length)
                // 실제 데이터(body) 넣기
                .body(content.getBytes());
    }
}