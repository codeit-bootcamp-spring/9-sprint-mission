package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.readStatus.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.readStatus.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.Locked;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController implements ReadStatusApi {
    private final UserService userService;
    private final ChannelService channelService;
    private final ReadStatusService readStatusService;

    @PostMapping
    public ResponseEntity<ReadStatus> create(@RequestBody CreateReadStatusRequest request){

        if (userService.findByID(request.userId()) == null){
            throw new NoSuchElementException("create ReadStatus 오류 | 유저가 존재하지 않음: " + request.userId());
        }

        if (channelService.findByID(request.channelId()) == null){
            throw new NoSuchElementException("create ReadStatus 오류 | 채널이 존재하지 않음: " + request.channelId());
        }

        ReadStatus newReadStatus = readStatusService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newReadStatus);
    }

    @PutMapping("/{readStatusId}")
    public ResponseEntity<ReadStatus> update(@PathVariable UUID readStatusId
            , @RequestBody UpdateReadStatusRequest request){

        ReadStatus readStatus = readStatusService.update(readStatusId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(readStatus);
    }

    @GetMapping
    public ResponseEntity<List<ReadStatus>> findByUserId(@RequestParam(value = "userId") UUID userId){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(readStatusService.findAllbyUserId(userId));
    }
}
