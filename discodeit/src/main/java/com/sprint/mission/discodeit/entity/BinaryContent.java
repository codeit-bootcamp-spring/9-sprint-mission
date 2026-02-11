package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent {
    private final UUID id;
    private final Instant createdAt;
    private String fileName;
    private String contentType;
    private Long size;
    private UUID userId;
    private UUID messageId;

    public BinaryContent(String fileName, String contentType, Long size) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
//      this.userId = userId;
    }
}

/* Instant createdAt 업로드된 시간, String contentType; 보고서.pdf" 같은 이름과 "application/pdf" 같은 파일 형식을 저장
파일을 누가 올렸는지, 어떤 메세지가 포함된 파일인지
"생성자" 매개변수 받아온걸로 객체를 만듬
createdAt = Instant.now(); -> 데이터가 현재 생성된 시간
 */