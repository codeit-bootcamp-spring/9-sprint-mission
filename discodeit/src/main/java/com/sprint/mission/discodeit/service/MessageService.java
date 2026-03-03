package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;

/**
 * 메시지 서비스 인터페이스
 * 메시지 생성, 조회, 수정, 삭제 및 첨부파일 처리 기능을 정의합니다.
 */
public interface MessageService {
    /**
     * 메시지를 생성합니다 (첨부파일 포함 가능).
     *
     * @param request 메시지 생성 요청
     * @param attachmentRequests 첨부파일 목록
     * @return 생성된 메시지
     */
    Message create(MessageCreateRequest request, List<BinaryContentCreateRequest> attachmentRequests);

    /**
     * 특정 채널의 모든 메시지를 조회합니다.
     *
     * @param channelId 채널 ID
     * @return 해당 채널의 메시지 목록
     */
    List<Message> findAllByChannelId(UUID channelId);

    /**
     * 메시지 내용을 업데이트합니다.
     *
     * @param id 메시지 ID
     * @param request 메시지 업데이트 요청
     * @return 업데이트된 메시지
     */
    Message update(UUID id, MessageUpdateRequest request);

    /**
     * 메시지를 삭제합니다.
     *
     * @param id 삭제할 메시지 ID
     */
    void delete(UUID id);
}
