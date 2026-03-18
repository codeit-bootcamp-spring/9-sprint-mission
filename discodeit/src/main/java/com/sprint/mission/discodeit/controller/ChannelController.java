package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.response.PageResponse; // 🌟 공통 봉투
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable; // 🌟 페이징 추가
import org.springframework.data.domain.Slice; // 🌟 페이징 추가
import org.springframework.data.web.PageableDefault; // 🌟 기본 페이징 설정
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;
  private final PageResponseMapper pageResponseMapper;

  @PostMapping("/public")
  @Override
  public ResponseEntity<ChannelDto> create(@RequestBody PublicChannelCreateRequest request) {
    ChannelDto createdChannel = channelService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdChannel);
  }

  @PostMapping("/private")
  @Override
  public ResponseEntity<ChannelDto> create(@RequestBody PrivateChannelCreateRequest request) {
    ChannelDto createdChannel = channelService.create(request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(createdChannel);
  }

  @PatchMapping("/{channelId}")
  @Override
  public ResponseEntity<ChannelDto> update(
      @PathVariable("channelId") UUID channelId,
      @RequestBody PublicChannelUpdateRequest request) {
    ChannelDto udpatedChannel = channelService.update(channelId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(udpatedChannel);
  }

  @DeleteMapping("/{channelId}")
  @Override
  public ResponseEntity<Void> delete(@PathVariable("channelId") UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @GetMapping("/user/{userId}")
  @Override
  public ResponseEntity<PageResponse<ChannelDto>> findAll(
      @PathVariable("userId") UUID userId,
      @PageableDefault(size = 50) Pageable pageable
  ) {
    Slice<ChannelDto> channelSlice = channelService.findAllByUserId(userId, pageable);
    PageResponse<ChannelDto> pageResponse = pageResponseMapper.fromSlice(channelSlice);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(pageResponse);
  }
}
