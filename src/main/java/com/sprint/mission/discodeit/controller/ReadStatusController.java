package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.service.ReadStatusService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/channels/{channelId}/read-status")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    public ReadStatusController(ReadStatusService readStatusService) {
        this.readStatusService = readStatusService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ReadStatusResponse markAsRead(
            @PathVariable UUID channelId,
            @RequestParam UUID userId
    ) {
        return readStatusService.markAsRead(userId, channelId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ReadStatusResponse get(
            @PathVariable UUID channelId,
            @RequestParam UUID userId
    ) {
        return readStatusService.findByUserAndChannel(userId, channelId);
    }
}
