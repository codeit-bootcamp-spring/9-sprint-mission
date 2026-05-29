package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ChannelService {

  // 퍼블릭 채널 생성 — CHANNEL_MANAGER 전용
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  ChannelDto createPublicChannel(PublicChannelCreateRequest request);

  // 퍼블릭 채널 수정 — CHANNEL_MANAGER 전용
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  ChannelDto update(UUID channelId, PublicChannelUpdateRequest request);

  // 퍼블릭 채널 삭제 — CHANNEL_MANAGER 전용
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  void delete(UUID channelId);

  // 조회는 인증된 사용자 모두 허용
  ChannelDto find(UUID channelId);
  List<ChannelDto> findAllByUserId(UUID userId);

  // 프라이빗 채널 생성은 별도 권한 불필요
  ChannelDto createPrivateChannel(PrivateChannelCreateRequest request);
}
