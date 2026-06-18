package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class FileStatusSseSendComponent {

  private final SseService sseService;

  public void sendFileStatusUpdate(UUID receiverId, BinaryContentDto binaryContentDto) {
    log.info("파일 업로드 상태 변경 발생 -> SSE 실시간 전송 시작 (binaryContents.updated)");

    String eventName = "binaryContents.updated";
    List<UUID> receiverIds = List.of(receiverId);

    sseService.send(receiverIds, eventName, binaryContentDto);

    log.info("파일 업로드 상태 변경 SSE 전송 완료!");
  }
}