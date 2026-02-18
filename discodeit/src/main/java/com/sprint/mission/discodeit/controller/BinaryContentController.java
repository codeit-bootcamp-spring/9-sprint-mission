package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/binary-contents", method = RequestMethod.POST)
    public ResponseEntity<BinaryContent> create(@RequestBody BinaryContentCreateRequest request) {
        return ResponseEntity.ok(binaryContentService.create(request));
    }
    //파일1개 조회 및 다운로드
    @RequestMapping(value = "/binary-contents/{id}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadOne(@PathVariable UUID id) {
        BinaryContent content = binaryContentService.find(id);
        //한글 파일명 안 깨지도록 인코딩 처리
        String encodedFileName = URLEncoder.encode(content.getFileName(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        return ResponseEntity.ok()
                //파일 형식을 지정
                .contentType(MediaType.parseMediaType(content.getContentType()))
                //파일 다운로드 시 파일 이름을 지정하는 헤더 설(헤더 : 안에 들어있는게 뭔지 설명해주는 겉포장지)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                //실제 파일 데이터를 바디에 담음
                .body(content.getBytes());
    }
    //파일 여러개 조회
    @RequestMapping(value = "/binary-contents", method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContent>> findAllByIds(@RequestParam List<UUID> ids) {
        return ResponseEntity.ok(binaryContentService.findAllByIdIn(ids));
    }}
