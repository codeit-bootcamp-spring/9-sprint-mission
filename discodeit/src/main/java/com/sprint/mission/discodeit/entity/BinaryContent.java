package com.sprint.mission.discodeit.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class BinaryContent {
//  UUID는 만든 고유 id값을 불러오기 위함
    private final UUID id;
//  byte는 이미지, 파일 등 바이너리 데이터를 표현하기 위함
    private final byte[] content;
//  contentType은 데이터의 종류
    private final String contentType;
//  createAt는 만든 시간을 알아야 하기 위해 필요함( 수정 개념이 필요하지 않음)
    private final Instant createdAt;

}
