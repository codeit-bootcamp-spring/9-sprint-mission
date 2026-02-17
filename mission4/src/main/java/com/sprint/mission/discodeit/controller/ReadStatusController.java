package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readstatus")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @RequestMapping(
            method= RequestMethod.POST
    )
    public ResponseEntity<ReadStatus> create(
            @RequestBody ReadStatusCreateRequest request
            ){
        ReadStatus readStatus = readStatusService.create(request);
        return ResponseEntity.ok(readStatus);
    }

    @RequestMapping(
            method = RequestMethod.PUT
    )
    public ResponseEntity<ReadStatus> update(
            @RequestParam UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
            ) {
        ReadStatus updateReadStatus= readStatusService.update(readStatusId,request);
        return ResponseEntity.ok(updateReadStatus);
    }

    @RequestMapping(
            method=RequestMethod.GET
    )
    public ResponseEntity<List<ReadStatus>> findByUserId(
            @RequestParam UUID userId
    ){
        List<ReadStatus> readStatusList = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(readStatusList);
    }


}
