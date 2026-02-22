package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusView;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @RequestMapping(method = RequestMethod.POST)
  public ReadStatusView create(@RequestBody ReadStatusCreateRequest request) {
    return readStatusService.create(request);
  }

  @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PATCH)
  public ReadStatusView update(
      @PathVariable UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest.Params params
  ) {
    return readStatusService.update(new ReadStatusUpdateRequest(readStatusId, params));
  }

  @RequestMapping(method = RequestMethod.GET)
  public List<ReadStatusView> findAllByUserId(@RequestParam("userId") UUID userId) {
    return readStatusService.findAllByUserId(userId);
  }
}
