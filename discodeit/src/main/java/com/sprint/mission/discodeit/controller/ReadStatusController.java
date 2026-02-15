package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readStatus.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.readStatus.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.Locked;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@ControllerAdvice
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController {
    private final UserService userService;
    private final ChannelService channelService;
    private final ReadStatusService readStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatus> create(@RequestBody CreateReadStatusRequest request){

        if (userService.findByID(request.userId()) == null){
            throw new NoSuchElementException("create ReadStatus 오류 | 유저가 존재하지 않음: " + request.userId());
        }

        if (channelService.findByID(request.channelId()) == null){
            throw new NoSuchElementException("create ReadStatus 오류 | 채널이 존재하지 않음: " + request.channelId());
        }

        ReadStatus newReadStatus = readStatusService.create(request);

        return ResponseEntity.ok(newReadStatus);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatus> update(@RequestBody UpdateReadStatusRequest request){

        ReadStatus readStatus = readStatusService.update(request);

        return ResponseEntity.ok(readStatus);
    }

    @RequestMapping(value = "{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> findByUserId(@PathVariable UUID userId){

        return ResponseEntity.ok(readStatusService.findAllbyUserId(userId));
    }
}
