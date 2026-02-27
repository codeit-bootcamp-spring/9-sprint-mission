package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusView;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ReadStatusController implements ReadStatusApi {

  private final ReadStatusService readStatusService;

  @Override
  public ReadStatusView create(
      ReadStatusCreateRequest request) {
    return readStatusService.create(request);
  }

  @Override
  public ReadStatusView update(
      UUID readStatusId,
      ReadStatusUpdateRequest.Params params
  ) {
    return readStatusService.update(new ReadStatusUpdateRequest(readStatusId, params));
  }

  @Override
  public List<ReadStatusView> findAllByUserId(
      UUID userId) {
    return readStatusService.findAllByUserId(userId);
  }
}
