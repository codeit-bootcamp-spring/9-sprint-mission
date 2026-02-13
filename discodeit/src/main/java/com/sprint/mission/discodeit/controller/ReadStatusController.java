package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

@ResponseBody
@RequestMapping("/api/readStatus")
@RequiredArgsConstructor
@Controller
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @RequestMapping(
            path = "createFromChannel",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<ReadStatus> createFromChannel(
            @RequestParam("channelId") UUID channelId,
            @RequestPart("readStatusCreateRequest")ReadStatusCreateRequest readStatusCreateRequest
    ){
        if (channelId == null) {
            return ResponseEntity.badRequest().build();
        }
        ReadStatus createdReadStatusFromChannel
                = readStatusService.create(readStatusCreateRequest);
        return ResponseEntity.
                status(HttpStatus.CREATED)
                .body(createdReadStatusFromChannel);
    }

    @RequestMapping(
            path = "updateFromChannel",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<ReadStatus> updateFromChannel(
            @RequestParam("readStatusId") UUID readStatusId,
            @RequestPart("readStatusUpdateRequest") ReadStatusUpdateRequest readStatusUpdateRequest
    ){
        if (readStatusId == null) {
            return ResponseEntity.badRequest().build();
        }
        ReadStatus updatedReadStatusFromChannel
                = readStatusService.update(readStatusId,readStatusUpdateRequest);
        return ResponseEntity.
                status(HttpStatus.OK)
                .body(updatedReadStatusFromChannel);
    }

    @RequestMapping(path = "findById")
    public ResponseEntity<ReadStatus> findById(
            @RequestParam("readStatusId") UUID readStatusId
    ){
        ReadStatus readStatus = readStatusService.find(readStatusId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(readStatus);
    }

    @RequestMapping(path = "findAllByUserId")
    public ResponseEntity<List<ReadStatus>> findAllByUserId(
            @RequestParam("userId") UUID userId
    ){
        List<ReadStatus> readStatus = readStatusService.findAllByUserId(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(readStatus);
    }
}
