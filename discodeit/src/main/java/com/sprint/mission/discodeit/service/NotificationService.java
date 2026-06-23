package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

  void createByMessage(MessageDto messageDto);

  void createByRole(User user, Role pastRole, Role newRole);

  void createByError(String errorMessage);

  List<NotificationDto> get(UUID userId);

  UUID delete(UUID notificationId);
}
