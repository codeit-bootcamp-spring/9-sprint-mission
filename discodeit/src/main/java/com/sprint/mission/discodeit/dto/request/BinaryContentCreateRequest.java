package com.sprint.mission.discodeit.dto.request;

import java.io.InputStream;

/**
 * 바이너리 콘텐츠 생성 요청 DTO
 * 이미지, PDF 등의 파일을 업로드할 때 사용합니다.
 *
 * @param fileName 파일명
 * @param contentType MIME 타입 (예: "image/jpeg", "application/pdf")
 * @param inputStream 파일 데이터 스트림
 */
public record BinaryContentCreateRequest(
        String fileName,
        String contentType,
        InputStream inputStream
) {
}
