package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;

/**
 * 채널 서비스 인터페이스
 * 공개/비공개 채널 생성, 조회, 수정, 삭제 기능을 정의합니다.
 */
public interface ChannelService {
    /**
     * 공개 채널을 생성합니다.
     *
     * @param request 공개 채널 생성 요청
     * @return 생성된 채널
     */
    Channel createPublic(PublicChannelCreateRequest request);

    /**
     * 비공개 채널을 생성합니다.
     *
     * @param request 비공개 채널 생성 요청
     * @return 생성된 채널
     */
    Channel createPrivate(PrivateChannelCreateRequest request);

    /**
     * 특정 사용자가 접근 가능한 모든 채널을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자가 접근 가능한 채널 목록
     */
    List<ChannelDto> findAllByUserId(UUID userId);

    /**
     * 채널 정보를 업데이트합니다.
     *
     * @param id 채널 ID
     * @param request 채널 업데이트 요청
     * @return 업데이트된 채널
     */
    Channel update(UUID id, PublicChannelUpdateRequest request);

    /**
     * 채널을 삭제합니다.
     *
     * @param id 삭제할 채널 ID
     */
    void delete(UUID id);
}
