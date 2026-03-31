package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController implements ChannelApi {
    private final ChannelService channelService;

    @PostMapping("/public")
    public ResponseEntity<ChannelDto> createPublic(@Valid @RequestBody PublicChannelCreateRequest request){
        log.info("Public 채널 생성 요청 수신: channelName={}", request.name());
        ChannelDto response = channelService.createPublicChannel(request);
        log.debug("Public 채널 생성 요청 처리 완료: channelName={}", request.name());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/private")
    public ResponseEntity<ChannelDto> createPrivate(@Valid @RequestBody PrivateChannelCreateRequest request){
        log.info("Private 채널 생성 요청 수신: participantIds={}", request.participantIds());
        ChannelDto response = channelService.createPrivateChannel(request);
        log.debug("Private 채널 생성 요청 처리 완료: participantIds={}", request.participantIds());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping("/{channelId}")
    public ResponseEntity<ChannelDto> update(@PathVariable UUID channelId
        , @Valid @RequestBody ChannelUpdateRequest request){
        log.info("채널 정보 수정 요청 수신: channelId={}", channelId);
        ChannelDto response = channelService.update(channelId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> delete(@PathVariable UUID channelId){
        log.info("채널 삭제 요청 수신: channelId={}", channelId);
        channelService.delete(channelId);
        log.debug("채널 삭제 요청 처리 완료: channelId={}", channelId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping
    public ResponseEntity<List<ChannelDto>> findByUserId(@RequestParam(value = "userId")  UUID userId){
        log.info("유저 별 채널 조회 요청 수신: userId={}", userId);
        List<ChannelDto> list = channelService.findAllByUserId(userId);
        log.debug("유저 별 채널 조회 요청 처리 완료: userId={}", userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);
    }
}

