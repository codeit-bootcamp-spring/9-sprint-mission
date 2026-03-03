package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * 바이너리 파일 콘텐츠를 저장하는 엔티티 클래스
 * 이미지, PDF 등의 바이너리 데이터를 저장하며, 불변(immutable) 객체입니다.
 */
@Getter
public class BinaryContent implements Serializable {
    /** 직렬화 버전 UID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 바이너리 콘텐츠 고유 식별자 */
    private final UUID id;

    /** 생성 일시 */
    private final Instant createdAt;

    /** 파일명 */
    private final String fileName;

    /** 파일 크기 (바이트) */
    private final long size;

    /** MIME 타입 (예: image/png, application/pdf) */
    private final String contentType;

    /** 실제 바이너리 데이터 */
    private final byte[] bytes;

    /**
     * 바이너리 콘텐츠 생성자
     *
     * @param fileName 파일명
     * @param contentType MIME 타입
     * @param bytes 실제 바이너리 데이터
     */
    public BinaryContent(String fileName, String contentType, byte[] bytes) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.size = bytes != null ? bytes.length : 0;
        this.contentType = contentType;
        this.bytes = bytes;
    }

}
