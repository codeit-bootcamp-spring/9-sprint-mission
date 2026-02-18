package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // 특정 채널의 수신 정보 생성
    @PostMapping("/channels/{channelId}/read-statuses")
    public ReadStatusDto createInChannel(
            @PathVariable UUID channelId,
            @RequestBody ReadStatusCreateRequest request
    ) {
        // URL channelId 로 덮어쓰기 (Message 쪽 패턴과 동일)
        ReadStatusCreateRequest overridden =
                new ReadStatusCreateRequest(request.userId(), channelId, request.lastReadAt());

        return ReadStatusDto.from(readStatusService.create(overridden));
    }

    //특정 사용자의 수신 정보 조회
    @GetMapping("/users/{userId}/read-statuses")
    public List<ReadStatusDto> findByUserId(@PathVariable UUID userId) {
        return readStatusService.findAllByUserId(userId).stream()
                .map(ReadStatusDto::from)
                .toList();
    }

    // (옵션) 단건 조회
    @GetMapping("/read-statuses/{readStatusId}")
    public ReadStatusDto find(@PathVariable UUID readStatusId) {
        return ReadStatusDto.from(readStatusService.find(readStatusId));
    }

    // (옵션) 수정
    @PutMapping("/read-statuses/{readStatusId}")
    public ReadStatusDto update(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    ) {
        return ReadStatusDto.from(readStatusService.update(readStatusId, request));
    }

    // (옵션) 삭제
    @DeleteMapping("/read-statuses/{readStatusId}")
    public void delete(@PathVariable UUID readStatusId) {
        readStatusService.delete(readStatusId);
    }

}
