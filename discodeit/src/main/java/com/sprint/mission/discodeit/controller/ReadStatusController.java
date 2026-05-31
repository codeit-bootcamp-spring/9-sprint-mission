package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController implements ReadStatusApi {
    private final ReadStatusService readStatusService;

    @PostMapping
    public ResponseEntity<ReadStatusDto> create(@Valid @RequestBody ReadStatusCreateRequest request){
        ReadStatusDto newReadStatus = readStatusService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newReadStatus);
    }

    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusDto> update(@PathVariable UUID readStatusId
            , @RequestBody ReadStatusUpdateRequest request){

        ReadStatusDto readStatus = readStatusService.update(readStatusId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(readStatus);
    }

    @GetMapping
    public ResponseEntity<List<ReadStatusDto>> findByUserId(@RequestParam(value = "userId") UUID userId){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(readStatusService.findAllbyUserId(userId));
    }
}
