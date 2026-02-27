package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserStatusApi;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusView;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserStatusController implements UserStatusApi {

  private final UserStatusService userStatusService;

  @Override
  public UserStatusView updateOnline(
      UUID userId,
      UserStatusUpdateRequest.Params params
  ) {
    return userStatusService.updateByUserId(userId, params);
  }
}