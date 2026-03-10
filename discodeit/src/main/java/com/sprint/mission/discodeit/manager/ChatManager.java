package com.sprint.mission.discodeit.manager;

import com.sprint.mission.discodeit.dto.response.MessageDto; // Entity 대신 DTO 임포트
import java.util.Optional;
import java.util.UUID;

public interface ChatManager {

  /**
   * 메시지를 전송하고 결과를 DTO로 반환합니다.
   *
   * @param userId    발신자 ID
   * @param channelId 채널 ID
   * @param content   메시지 내용
   * @return 전송된 메시지 DTO (Optional)
   */
  Optional<MessageDto> sendMessage(UUID userId, UUID channelId, String content);

  /**
   * 메시지 ID를 통해 작성자의 이름을 조회합니다.
   *
   * @param messageId 메시지 ID
   * @return 작성자 이름 (없을 경우 "Unknown")
   */
  String getAuthorName(UUID messageId);
}