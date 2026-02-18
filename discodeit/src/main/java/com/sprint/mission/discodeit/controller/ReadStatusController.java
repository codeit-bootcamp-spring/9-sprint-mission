package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Retention;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    //단톡방 처음 들어갔을 때 읽기 시작했으니까 기록 시작

    @RequestMapping(value = "/read-statuses", method = RequestMethod.POST)
    public ResponseEntity<ReadStatus> create (@RequestBody ReadStatusCreateRequest request) {
        return ResponseEntity.ok(readStatusService.create(request));
    }
    //채팅방에서 메시지를 계속 읽어 내려가면서 마지막으로 읽은 시점을 최신화
    @RequestMapping(value = "/read-statuses/{id}", method = RequestMethod.PUT)
    public ResponseEntity<ReadStatus> update(@PathVariable UUID id, @RequestBody ReadStatusUpdateRequest request) {
        return ResponseEntity.ok(readStatusService.update(id, request));
    }
    //나중에 다시 접속했을 때 "내가 이 방에서 안 읽은 메시지가 몇개지?" 를 계산하기위해 조회가 필요
    @RequestMapping(value = "/read-statuses", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> findAllByUserId(@RequestParam UUID userId){
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }
}
