package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface NotificationService {

  List<NotificationDto> findAllByReceiverId(UUID receiverId);

  void delete(UUID notificationId, UUID receiverId);

  void create(Set<UUID> receiverIds, String title, String content);
}